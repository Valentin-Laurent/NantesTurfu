package fr.turfu.nantesturfu;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class DetailsActivity extends ListActivity {
    private String currentFavoris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 1);
        GestionFavoris gestionFav = new GestionFavoris(getApplicationContext());
        ArrayList<String> arrayFavoris = gestionFav.getFav();
        currentFavoris = arrayFavoris.get(position-1);

        setContentView(R.layout.activity_details);
        overridePendingTransition(0, 0); //Permet de ne pas avoir d'animation à l'ouverture de l'activité


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
        bar.setTitle(currentFavoris);
    }

    //Permet de ne pas avoir d'animation à la fermeture de l'activité
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}