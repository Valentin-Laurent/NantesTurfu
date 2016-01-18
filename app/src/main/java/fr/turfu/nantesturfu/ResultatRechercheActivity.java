package fr.turfu.nantesturfu;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResultatRechercheActivity extends ListActivity {

    private String recherche; //La chaine de caractère qui contiendra ce que l'utilisateur à recherché.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultat_recherche);

        //On récupère l'intent généré par la recherche
        Intent i = getIntent();
        overridePendingTransition(0,0); //Permet de ne pas avoir d'animation à l'ouverture de l'activité
        if (Intent.ACTION_SEARCH.equals(i.getAction())) {
            recherche = i.getStringExtra(SearchManager.QUERY);
        }

        //On récupère la liste des stations de Nantes
        List<StationBicloo> stationsBicloo = null;
        try {
            StationsBiclooXMLParser parser = new StationsBiclooXMLParser();
            stationsBicloo = parser.parse(getAssets().open("stationsBicloo.xml"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        //On cherche les stations qui contiennent le mot recherché
        ArrayList<StationBicloo> resultatRecherche = new ArrayList<>(); //La liste qui va contenir les résultats sous la forme d'objets StationBicloo
        ArrayList<String> arrayResultat = new ArrayList<>();            //La liste qui va contenir les adresse de ces stations
        arrayResultat.add("Cliquez sur un résultat pour afficher le détail");

        for (StationBicloo s : stationsBicloo) {
            if (s.getName().toLowerCase().contains(recherche.toLowerCase()))  { //On recherche dans les noms
                resultatRecherche.add(s);
                arrayResultat.add(s.getAddress());
            }
        }

        int taille = arrayResultat.size();

        if (taille==0) {
            arrayResultat.clear(); //On enlève le texte "Cliquez sur un résultat pour afficher le détail"
            arrayResultat.add("Il n'y a pas de station correspondant à votre recherche.");
        }

        //On gère l'affichage des résultats
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayResultat);
        setListAdapter(adapter);

    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        ListView myListView = getListView();
        if (position != 0) { //On ne lance pas l'activité pour le premier item de la liste car il correspond au texte "Cliquez sur un résultat pour afficher le détail"
            Intent intent = new Intent(this, DetailsActivity.class);
            startActivity(intent);
        }
        //String itemClicked = (String) myListView.getAdapter().getItem(position);
    }
    //Code recopié (la classe ListActivity gère très mal les toolbars, c'est une solution "bricolage", qui utilise une autre toolbar : "toolbar_simplifiee")
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar_simplifiee, root, false);
        root.addView(bar, 0);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //On set le titre qui sera affiché dans la toolbar
        bar.setTitle("Recherche pour : " + recherche);
    }

    //Permet de ne pas avoir d'animation à la fermeture de l'activité
    @Override public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

}
