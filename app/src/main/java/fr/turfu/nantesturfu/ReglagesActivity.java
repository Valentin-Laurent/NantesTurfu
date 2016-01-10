package fr.turfu.nantesturfu;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class ReglagesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.reglages);
        setContentView(R.layout.activity_reglages);

    }
    //Code recopié (la classe PreferenceActivity gère très mal les toolbars, c'est une solution "bricolage", qui utilise une autre toolbar : "toolbar_simplifiee")
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
        bar.setTitle("Réglages");
    }

    //Permet de ne pas avoir d'animation à la fermeture de l'activité
    @Override public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
