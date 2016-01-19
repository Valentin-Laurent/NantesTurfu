package fr.turfu.nantesturfu;

import android.location.Location;

import org.osmdroid.util.GeoPoint;

import java.math.BigDecimal;
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
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

/**
 * Created by Valentin on 13/01/2016.
 */
public class StationBicloo {

    private String name;
    private int number;
    private String address;
    private BigDecimal lat;
    private BigDecimal lng;
    private int Ntot;
    private int Nvide;
    private int Nvelos;

    public int getNtot() {
        return Ntot;
    }

    public void setNtot(int ntot) {
        Ntot = ntot;
    }

    public int getNvide() {
        return Nvide;
    }

    public void setNvide(int nvide) {
        Nvide = nvide;
    }

    public int getNvelos() {
        return Nvelos;
    }

    public void setNvelos(int nvelos) {
        Nvelos = nvelos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String adress) {
        this.address = adress;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }
    public GeoPoint getLoc(){
       GeoPoint res= new GeoPoint(lat.doubleValue(),lng.doubleValue());
        return res;
    }
}

