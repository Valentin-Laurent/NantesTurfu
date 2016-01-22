package fr.turfu.nantesturfu;

/**
 * Created by FT on 19/01/2016.
 * Le parser est déclenché par Mapactivity ou par Favoris Activity
 */

    import android.app.Activity;
    import android.os.AsyncTask;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.InputStreamReader;
    import java.io.UnsupportedEncodingException;
    import java.net.URL;
    import java.net.URLConnection;
    import java.net.URLEncoder;
    import javax.json.Json;
    import javax.json.stream.JsonParser;
    import javax.json.stream.JsonParser.Event;


    public class Jparser extends AsyncTask<StationBicloo, Void, Void> {
        Activity activiteSource;
        public Jparser(Activity activiteSource){
           this.activiteSource=activiteSource;
        }

        /**
         * Cette methode prend en entrée une StationBicloo
         * Elle permet d'executer de façon asynchrone la requete de l'API sans bloquer l'application.
         * @param params
         * @return
         */
        @Override
        protected Void doInBackground(StationBicloo... params) {
            StationBicloo sortie = params[0];
            String url = "https://api.jcdecaux.com/vls/v1/stations/";
            String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
            String station = Integer.toString(sortie.getNumber());
            String contract = "Nantes";
            String apiKey ="f9b96dbf0a96c1c4fc5040d47999ebc8bec106b7";
            String query = null;
            try {
                query = String.format("contract=%s&apiKey=%s",
                        URLEncoder.encode(contract, charset),
                        URLEncoder.encode(apiKey, charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            URLConnection connection = null;
            try {
                connection = new URL(url+ station + "?" + query).openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.setRequestProperty("Accept-Charset", charset);
            InputStream response = null;
            try {
                response = connection.getInputStream();
            JsonParser parser = Json.createParser(new InputStreamReader(response));
            Event event = parser.next(); // START_OBJECT
            event = parser.next();       // KEY_NAME
            event = parser.next();       // num
            event = parser.next();       // key name
            event = parser.next();       //  name
            event = parser.next();       // key address
            event = parser.next();       // address
            event = parser.next();       // key pos
            event = parser.next();       //start object
            event = parser.next();    //key lat
            event = parser.next();  //lat
            event = parser.next();    //long key
            event = parser.next(); // longitude
            event = parser.next();    //end of position
            event = parser.next();    //banking key
            event = parser.next();    //boolean bank a faire
// le boolean non géré
            event = parser.next();    //bonus key
            event = parser.next();    //bonus
            event = parser.next();    //status key
            event = parser.next();    //status
            event = parser.next();    //contract key
            event = parser.next();    //contract
            event = parser.next();    //bike stands
            event = parser.next();    //place totale
            sortie.setNtot(parser.getInt());
            event = parser.next();    //dispo key
            event = parser.next();    //places dispo
            sortie.setNvide(parser.getInt());
            event = parser.next();    //velos key
            event = parser.next();    //velos
            sortie.setNvelos(parser.getInt());
            parser.close();

            //On test quelle activité a fait l'appel du parser
            if (activiteSource instanceof MapActivity) {
                ((MapActivity) activiteSource).addicon(sortie);
            }
            else if (activiteSource instanceof MainActivity) {
                String afficher = Integer.toString(sortie.getNvelos())+"/"+Integer.toString(sortie.getNtot());
                ((MainActivity) activiteSource).nbVelos.add(afficher);
            }

            return null;
            } catch (IOException e) {
                if (activiteSource instanceof MainActivity) {
                    ((MainActivity) activiteSource).nbVelos.add("Pas de connection");
                }
                return null;
            }
        }
    }


