package fr.turfu.nantesturfu;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class ResultatRechercheActivity extends ListActivity {

    String recherche; //La chaine de caractère qui contiendra ce que l'utilisateur à recherché.

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
        bar.setTitle("Recherche pour : "+recherche);
    }

    //Permet de ne pas avoir d'animation à la fermeture de l'activité
    @Override public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
