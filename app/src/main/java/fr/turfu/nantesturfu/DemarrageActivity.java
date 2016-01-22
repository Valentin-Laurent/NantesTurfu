package fr.turfu.nantesturfu;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//Classe qui sert à choisir quelle activité on lance au démarrage
public class DemarrageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //On lance l'activité map ou favoris suivant les réglages
        String p = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Menu par défaut", "0");
        if (p.equals("1")) {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent2 = new Intent(this, MainActivity.class);
            startActivity(intent2);
        }
        this.finish();
    }
}
