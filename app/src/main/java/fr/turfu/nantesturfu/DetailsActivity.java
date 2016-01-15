package fr.turfu.nantesturfu;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        overridePendingTransition(0, 0); //Permet de ne pas avoir d'animation à l'ouverture de l'activité
    }


    //Permet de ne pas avoir d'animation à la fermeture de l'activité
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}