package fr.turfu.nantesturfu;

import java.math.BigDecimal;

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
}
