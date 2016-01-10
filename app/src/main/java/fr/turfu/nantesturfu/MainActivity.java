package fr.turfu.nantesturfu;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    Button b;

    /*private View.OnClickListener imcListener = new View.OnClickListener() {
        @Override
        public void onClick(View vue) {
            double p = Double.parseDouble((((TextView) findViewById(R.id.Poids)).getText()).toString());
            double t = Double.parseDouble((((TextView) findViewById(R.id.Taille)).getText()).toString());
            TextView resultat = (TextView) findViewById(R.id.Resultat);
            resultat.setText(Double.toString(p/(Math.pow(t,2))));
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar menu = getSupportActionBar();

        menu.setDisplayShowTitleEnabled(false);


        // b = (Button) findViewById(R.id.BouttonIMC);
        //b.setOnClickListener(imcListener);
        new CallAPI().execute("https://api.jcdecaux.com/vls/v1/stations/10042?contract=paris&apiKey=1585b03813a6d3d94529262d9a01b8ba02a33ecb");

    }

    //La méthode suivante permet d'inflater le fichier menu.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.préférences:
                Intent intent = new Intent(this, ReglagesActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0); //Permet de ne pas avoir d'animation à l'ouverture de l'activité
                return true;

            case R.id.favoris:
                return true;

            case R.id.search:
                return true;

            case R.id.carte:
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
    //Classe qui permet d'effectuer la requete GET hors du thread UI
    private class CallAPI extends AsyncTask<String, String, String> {

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

    }
}