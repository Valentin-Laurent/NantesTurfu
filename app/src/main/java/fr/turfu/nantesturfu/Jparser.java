package fr.turfu.nantesturfu;

/**
 * Created by FT on 19/01/2016.
 */

    import android.app.Activity;
    import android.os.AsyncTask;

    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.InputStreamReader;
    import java.io.StringReader;
    import java.io.UnsupportedEncodingException;
    import java.math.BigDecimal;
    import java.net.MalformedURLException;
    import java.net.URL;
    import java.net.URLConnection;
    import java.net.URLEncoder;
    import java.util.IllegalFormatException;

    import javax.json.Json;
    import javax.json.stream.JsonParser;
    import javax.json.stream.JsonParser.Event;

    /**
     *
     * @author FT
     */

    public class Jparser extends AsyncTask<StationBicloo, Void, Void> {
        Activity activiteSource;
        public Jparser(MapActivity activiteSource){
           this.activiteSource=activiteSource;
        }
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

// Cela permet de printer la reponse de la requete pour tester
 /*   BufferedReader in = new BufferedReader(new InputStreamReader(response));
String line = null;
while((line = in.readLine()) != null) {
  System.out.println(line);
}*/
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
            //BigDecimal lng = parser.getBigDecimal();
            // System.out.println(lng.toString());
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

            //On test quelle activité à appelé le parser
            if (activiteSource instanceof MapActivity) {
                ((MapActivity) activiteSource).addicon(sortie);
            }

            return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(StationBicloo stat) {
        // update item layer
        }
    }


