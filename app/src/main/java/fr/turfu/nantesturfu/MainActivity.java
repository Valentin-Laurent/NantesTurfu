package fr.turfu.nantesturfu;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*Flox
Nettoyer et commenter code (enlever les classes useless)
Javadoc

Si t'as le temps :
Refresh la map à chaque ajout d'icone
Rotation
*/

/*Val
Nettoyer et commenter code
Javadoc

Si j'ai le temps :
Factoriser le code qui gère la toolbar
*/

/**
 * Activité qui gère les favoris. Elle s'appelle Main car à l'origine ça a été la première activité du projet
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public ArrayList<String> nbVelos; //L'attribut nbVelos est modifié par Jparser. Pas de getter/setter, le projet est relativement petit pour mettre cet attribut en public
    //La méthode onClick de la boite de dialogue (déclarée dans la méthode onItemClick) a besoin d'accéder ces variables
    private ArrayList<String> arrayFavoris;
    private int position;

    /**
     * La méthode principale de la classe. Elle récupère les favoris, les compare à la liste des stations Bicloo, va cherche les informations des stations corresponantes, initialise les vues (toolbar, listview) pour afficher le tout
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nbVelos = new ArrayList<>(); //Va contenir une liste de nombre de vélos dispo/nombre de vélo total : "15/20","0/18", etc...
        setContentView(R.layout.activity_main);

        //On gère la toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar menu = getSupportActionBar();
        menu.setDisplayShowTitleEnabled(false); //On n'affiche pas le titre de l'appli
        toolbar.setTitle("Favoris");//On affiche par contre le titre de l'activité

        //On récupère les favoris dans une liste :
        GestionFavoris gestionFav = new GestionFavoris(getApplicationContext());
        arrayFavoris = gestionFav.getFav();

        //On récupère la liste des stations de Nantes
        List<StationBicloo> stationsBicloo = null;
        try {
            StationsBiclooXMLParser parser = new StationsBiclooXMLParser();
            stationsBicloo = parser.parse(getAssets().open("stationsBicloo.xml"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        //On récupère le nombre de Bicloo disponibles pour chaque station favorite dans une liste :
        for (String f:arrayFavoris) {
            for (StationBicloo s:stationsBicloo) {
                if (f.equals(s.getAddress())) {
                    Jparser parser = new Jparser(this);
                    parser.execute(s); //Le parser modifie lui-même la variable nbVelos
                    //Cette instruction permet d'attendre que le parser ai fini son execution pour continuer.
                    //Elle est indispensable sinon la méthode onCreate se poursuit sans que nbVélos soit actualisée et le code qui suit devient incohérent
                    try {
                        parser.get(1500,TimeUnit.MILLISECONDS);
                    }
                    catch (Exception e) {
                        nbVelos.add("Problème de connection à Internet");
                    }
                }
            }
        }

        //On déclare une liste de HashMap, chaque HashMap va contenir le nom de la station ainsi que la fameuse variable nbVélos
        List<HashMap<String, String>> listeFavoris = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> element;
        if (arrayFavoris.size()==0) { //Si l'utilisateur n'a pas de favoris :
            element = new HashMap<>();
            element.put("nom", "Vous n'avez pas de favoris");
            listeFavoris.add(element);
        }
        else if (arrayFavoris.size()> 0) {  //Sinon :
            element = new HashMap<>();
            element.put("nom", "Cliquez sur un résultat pour supprimer la station des favoris");
            listeFavoris.add(element);
            for (int j = 0; j < arrayFavoris.size(); j++) {
                element = new HashMap<>();
                element.put("nom", arrayFavoris.get(j));
                element.put("infos", nbVelos.get(j));
                listeFavoris.add(element);
            }
        }

        //On gère l'affichage de la liste de HashMap
        ListAdapter adapter = new SimpleAdapter(this,listeFavoris,android.R.layout.simple_list_item_2,new String[] {"nom", "infos"},new int[] {android.R.id.text1, android.R.id.text2 });
        ListView listView = (ListView) findViewById(R.id.listeFavoris);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this); //Pour lancer la méthode onItemClic

    }

    /**
     * Méthode qui permet d'intialiser la barre de menu à l'aide du fichier menu.xml et de prendre en charge la recherche
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        //On sérialise le fichier menu.xml pour l'afficher dans la barre de menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_favoris, menu);

        // Ici on gère le fait qu'on pourra lancer une recherche depuis cette activité
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    /**
     * Méthode pour gérer le clic sur les boutons du menu (sauf la recherche qui est gérée directement par le search manager
     * @param item
     * @return retourne un booléen pour dire que l'action a été effectuée
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.préférences:
                Intent intent = new Intent(this, ReglagesActivity.class);
                startActivity(intent);
                return true;

            case R.id.carte:
                Intent intent2 = new Intent(this, MapActivity.class);
                startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Cette méthode permet de gérer le clic sur la liste des favoris pour retirer un favoris
     * @param position La position du favoris sur lequel on a cliqué
     */
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        this.position = position;
        if (position > 0) { //On ne déclenche rien si l'user clique sur le texte "Cliquez sur un résultat pour supprimer la station des favoris"
            new AlertDialog.Builder(this) //On déclenche une fenêtre pour confirmer l'action
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Voulez-vous vraiment supprimer ce favoris ?")
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //On retire le favoris puis on redémarre l'activité pour afficher la liste mise à jour
                            GestionFavoris gestionFav = new GestionFavoris(MainActivity.this);
                            gestionFav.deleteFav(arrayFavoris.get(MainActivity.this.position - 1));
                            MainActivity.this.recreate();
                        }
                    })
                    .setNegativeButton("Non", null)
                    .show();
        }

    }

    /**
     *Est appelée lorsque l'activité mise en pause a de nouveau le focus
     *Refresh les favoris si la liste à changé dans le cas d'un ajout de favoris
     * Note : visiblement cela ne marche pas, c'est sûrement du au fait que cette activité est lancée en mode SingleTask
     */
    @Override public void onResume() {
        super.onResume();
        GestionFavoris gestionFavoris = new GestionFavoris(this.getApplicationContext());
        if (arrayFavoris.size() != gestionFavoris.getFav().size()) { //Si la liste de favoris a changé
            this.recreate();
        }
    }
}