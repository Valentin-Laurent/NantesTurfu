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
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//Map : un click refresh le bordel, un clic long : lance l'activité "DétailActivity" (Flox)
//Map : cosmétique (à la fin si on a la temps) : harmoniser les barres de menu, faire un bel affichage du menu sur smartphone
//Faire l'activité DétailActivity
//Faire l'activité Favoris (notamment regarder la meilleure façon de stocker les favoris)
//Scraper les stations de la TAN
//Faire un parser pour TAN et Bicloo
//Utiliser le réglage pour le choix de l'activité lancée en premier (carte ou favoris)


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //On gère la toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar menu = getSupportActionBar();
        menu.setDisplayShowTitleEnabled(false); //On n'affiche pas le titre de l'appli
        toolbar.setTitle("Favoris");//On affiche par contre le titre de l'activité


        //test d'appel à l'API
        //new CallAPI().execute("https://api.jcdecaux.com/vls/v1/stations/10042?contract=paris&apiKey=1585b03813a6d3d94529262d9a01b8ba02a33ecb");

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