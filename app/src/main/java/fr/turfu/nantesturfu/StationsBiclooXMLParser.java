package fr.turfu.nantesturfu; /**
 * Created by Valentin on 13/01/2016.
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.math.BigDecimal;


public class StationsBiclooXMLParser {

    List<StationBicloo> stationBicloos;
    private StationBicloo stationBicloo;
    private String text;

    public StationsBiclooXMLParser() {
        stationBicloos = new ArrayList<StationBicloo>();
    }

    public List<StationBicloo> getstationBicloos() {
        return stationBicloos;
    }

    public List<StationBicloo> parse(InputStream is) {
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("stationBicloo")) {
                            // create a new instance of stationBicloo
                            stationBicloo = new StationBicloo();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("stationBicloo")) {
                            // add stationBicloo object to list
                            stationBicloos.add(stationBicloo);
                        } else if (tagname.equalsIgnoreCase("name")) {
                            stationBicloo.setName(text);
                        } else if (tagname.equalsIgnoreCase("number")) {
                            stationBicloo.setNumber(Integer.parseInt(text));
                        } else if (tagname.equalsIgnoreCase("address")) {
                            stationBicloo.setAddress(text);
                        } else if (tagname.equalsIgnoreCase("lat")) {
                            stationBicloo.setLat(new BigDecimal(text));
                        } else if (tagname.equalsIgnoreCase("lng")) {
                            stationBicloo.setLng(new BigDecimal(text));
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stationBicloos;
    }
}