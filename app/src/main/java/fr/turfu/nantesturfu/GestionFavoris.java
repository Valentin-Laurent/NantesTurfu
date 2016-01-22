package fr.turfu.nantesturfu;

import android.content.Context;

import java.util.ArrayList;

/**
 * Cette classe utilise la classe TinyDB et permet de gérer très facilement les favoris
 */
public class GestionFavoris {
    private TinyDB tinydb;

    /**
     *Le constructeur est simple, il suffit de passer le context
     * @param context
     */
    public GestionFavoris(Context context) {
        tinydb = new TinyDB(context);
    }

    /**
     * Ajoute un favoris
     * @param nom Le nom du favoris
     */
    public void addFav(String nom) {
        ArrayList<String> listeFav = tinydb.getListString("cléFav");
        listeFav.add(nom);
        tinydb.putListString("cléFav",listeFav);

    }

    /**
     * Récupère les favoris
     * @return la liste des favoris
     */
    public ArrayList<String> getFav() {
        return tinydb.getListString("cléFav");
    }

    /**
     * Supprimer tous les favoris (méthode qui n'est jamais appelée, utile pour débugger
     */
    public void clearFav() {
        ArrayList<String> listeFav = new ArrayList<>();
        tinydb.putListString("cléFav",listeFav);
    }

    /**
     * Supprime un favoris
     * @param nom Le nom du favoris a supprimer
     */
    public void deleteFav(String nom) {
        ArrayList<String> listeFav = tinydb.getListString("cléFav");
        listeFav.remove(nom);
        tinydb.putListString("cléFav",listeFav);
    }
}