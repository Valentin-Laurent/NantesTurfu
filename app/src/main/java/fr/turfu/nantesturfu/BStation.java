package fr.turfu.nantesturfu;

import org.osmdroid.util.GeoPoint;

/**
 * Created by FT on 08/01/2016.
 */
public class BStation extends Station{
    public int dispo;
    public int total;
    boolean cb;

    public BStation(GeoPoint p) {
        pos = p;
        nom = "50 Otages";
        dispo=10;
        total=20;
    }
    public String toString(){
        String res=Integer.toString(dispo)+" / "+Integer.toString(total);
        return res;
    }
}

