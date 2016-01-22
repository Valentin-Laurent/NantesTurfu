package fr.turfu.nantesturfu;

import org.osmdroid.util.GeoPoint;

import java.math.BigDecimal;


/**
 * Classe qui contient les informations concernant une station
 */
public class StationBicloo {
    /**
     * nom - numero -  adresse - latitude - longitude - nombre total de places - places dispo - places occupées
     */
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

