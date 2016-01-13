package fr.turfu.nantesturfu;

import android.location.Location;
import org.osmdroid.util.GeoPoint;

/**
 * Created by FT on 08/01/2016.
 */
public abstract class Station {
    public GeoPoint pos;
    public String nom;

    public Station(){
        pos= new GeoPoint(47.214479, -1.555768);
        nom="Default";
    }
    public Station(GeoPoint p) {
        pos=p;
        nom="Commerce";
    }
    public abstract String toString();
}

