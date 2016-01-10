package fr.turfu.nantesturfu;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ResultatRechercheActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultat_recherche);

        Intent i = getIntent();
        if (Intent.ACTION_SEARCH.equals(i.getAction())) {
        }
    }
}
