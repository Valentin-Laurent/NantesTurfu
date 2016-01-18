package fr.turfu.nantesturfu;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Valentin on 15/01/2016.
 */
public class GestionFavoris {
    private TinyDB tinydb;

    public GestionFavoris(Context context) {
        tinydb = new TinyDB(context);
    }

    public void addFav(String nom) {
        ArrayList<String> listeFav = tinydb.getListString("cléFav");
        listeFav.add(nom);
        tinydb.putListString("cléFav",listeFav);

    }

    public ArrayList<String> getFav() {
        return tinydb.getListString("cléFav");
    }

    public void clearFav() {
        ArrayList<String> listeFav = new ArrayList<>();
        tinydb.putListString("cléFav",listeFav);
    }

    public void deleteFav(String nom) {
        ArrayList<String> listeFav = tinydb.getListString("cléFav");
        listeFav.remove(nom);
        tinydb.putListString("cléFav",listeFav);
    }
}