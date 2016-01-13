package fr.turfu.nantesturfu;

import org.osmdroid.util.GeoPoint;

/**
 * Created by FT on 08/01/2016.
 */
public class TStation extends Station{

    public TStation(GeoPoint p){
        super(p);

    }
    public String toString(){
        String res="tostring de station de tram";
        return res;
    }
}
