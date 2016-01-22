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

/*Flox
Licenses etc.
Nettoyer et commenter code (enlever les classes useless)
Javadoc
Affichage des stations hors connection
Toast pas de connection sur la carte

Si t'as le temps :
Refresh la map à chaque ajout d'icone
Rotation
*/

/*Val
Nettoyer et commenter code
Javadoc
Virer détail activity

Si j'ai le temps :
Factoriser le code qui gère la toolbar
Gérer les tasks pour avoir un bouton retour logique quand on commence l'appli par carte
*/


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public ArrayList<String> nbVelos;
    private ArrayList<String> arrayFavoris;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nbVelos = new ArrayList<>();

        //On lance l'activité map à la place de celle-ci suivant les préférences de l'utilisateur
        String p = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Menu par défaut", "0");
        Intent intent = getIntent();
        if ((p.equals("1"))&&(true)) {
            Intent intent2 = new Intent(this, MapActivity.class);
            startActivity(intent2);
            //this.finish(); //On arrête cette activité (évite notamment un comportement étrange pour l'utilisateur si il appuie sur retour)
        }

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
                    parser.execute(s); //Le parser modifie la liste les horaires qui sont public
                    try {
                        parser.get();
                        String boucle = "ok";
                    }
                    catch (Exception e) {
                        nbVelos.add("Erreur");
                    }
                }
            }
        }

        //On déclare une liste de HashMap, chaque HashMap va contenir le nom de la station ainsi que le nombre de stations
        List<HashMap<String, String>> listeFavoris = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> element;
        if (arrayFavoris.size()==0) { //Si l'utilisateur n'a pas de favoris :
            element = new HashMap<>();
            element.put("nom", "Vous n'avez pas de favoris");
            listeFavoris.add(element);
        }
        else if (arrayFavoris.size()> 0) {                    //Sinon :
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


    //Méthode pour gérer le clic sur les bouttons du menu (sauf la recherche qui est gérée directement par le search manager
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.préférences:
                Intent intent = new Intent(this, ReglagesActivity.class);
                startActivity(intent);
                return true;

            case R.id.favoris:
                return true;

            case R.id.carte:
                Intent intent2 = new Intent(this, MapActivity.class);
                startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    //Cette méthode lance l'activité détail en passant le nom du favoris en paramètre
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        this.position = position;
        if (position > 0) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Voulez-vous vraiment supprimer ce favoris ?")
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            GestionFavoris gestionFav = new GestionFavoris(MainActivity.this);
                            gestionFav.deleteFav(arrayFavoris.get(MainActivity.this.position - 1));
                            MainActivity.this.recreate(); //On redémarre l'activité pour afficher la mise à jour

                        }
                    })
                    .setNegativeButton("Non", null)
                    .show();
        }

    }

    //Refresh les favoris si la liste à changé dans le cas d'un ajout de favoris
    @Override public void onResume() {
        super.onResume();
        GestionFavoris gestionFavoris = new GestionFavoris(this.getApplicationContext());
        if (arrayFavoris.size() != gestionFavoris.getFav().size()) {
            this.recreate();
        }
    }

    //Classe qui permet d'effectuer la requete GET hors du thread UI
    /*private class CallAPI extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            String urlString=params[0];

            try {
                //On fait le GET
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                //On récupère le contenu JSON dans un string builder
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                return sb.toString();
            } catch (Exception e ) {
                return e.getMessage();
            }

        }
        //On affiche le résultat dans le thread UI
        protected void onPostExecute(String resultat) {
            TextView test = (TextView) findViewById(R.id.Test);
            test.setText(resultat);
        }

    }*/
}