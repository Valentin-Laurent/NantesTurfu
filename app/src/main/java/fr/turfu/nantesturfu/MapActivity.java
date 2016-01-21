package fr.turfu.nantesturfu;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
public class MapActivity extends AppCompatActivity {

    private MapView myOpenMapView;
    private MapController myMapController;
    private DefaultResourceProxyImpl defaultResourceProxyImpl;
    LocationManager locationManager;
    ArrayList<OverlayItem> overlayItemArray;
    private Drawable bic ;
    private Drawable bic3 ;
    private Drawable bic_full ;
    private Drawable bic_empty ;
    //map center
    IGeoPoint c;
    GeoPoint center;

    /** onCreate = A l'ouverture de l'activité. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        overridePendingTransition(0, 0); //Permet de ne pas avoir d'animation à l'ouverture de l'activité

        //On gère la toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar menu = getSupportActionBar();
        menu.setDisplayShowTitleEnabled(false); //On n'affiche pas le titre de l'appli
        toolbar.setTitle("Carte");//On affiche par contre le titre de l'activité
        defaultResourceProxyImpl= new DefaultResourceProxyImpl(this);
        myOpenMapView = (MapView) findViewById(R.id.openmapview);
        //Activer le zoom et le tactile
        myOpenMapView.setBuiltInZoomControls(true);
        myOpenMapView.setMultiTouchControls(true);
        myMapController = (MapController) myOpenMapView.getController();
        myMapController.setZoom(15);
        bic = ContextCompat.getDrawable(this, R.drawable.bic);
        bic3 = ContextCompat.getDrawable(this, R.drawable.bic3);
        bic_full = ContextCompat.getDrawable(this, R.drawable.bic_full);
        bic_empty = ContextCompat.getDrawable(this, R.drawable.bic_empty);
        //--- Create Overlay
        overlayItemArray = new ArrayList<OverlayItem>();

        // AJOUTER LE POINT BLEU !
        MyItemizedIconOverlay myItemizedIconOverlay
                = new MyItemizedIconOverlay(
                overlayItemArray, null, defaultResourceProxyImpl);
        myOpenMapView.getOverlays().add(myItemizedIconOverlay);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //for demo, getLastKnownLocation from GPS only, not from NETWORK
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastLocation
                = locationManager.getLastKnownLocation(
                LocationManager.GPS_PROVIDER);
        if (lastLocation != null) {
            updateCenterLoc(lastLocation);
        }

        //Add Scale Bar
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(this);
        myOpenMapView.getOverlays().add(myScaleBarOverlay);

        /**
         * On récupère la liste des stations avec le Parser xml
         * On les classe par distance aux centre de l'écran
         * On appelle l'api bicloo pour chaque station de la liste et
         * on crée un overlay pour chaque qu'on ajoute a la mapview
         *
          */

        //On récupère la liste des stations de Nantes
        List<StationBicloo> stationsBicloo = null;
        try {
            StationsBiclooXMLParser parser = new StationsBiclooXMLParser();
            stationsBicloo = parser.parse(getAssets().open("stationsBicloo.xml"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        // Avant d'afficher on trie les stations par proximité au centre de l'ecran = position de l'utilisateur:
        c = myOpenMapView.getMapCenter();

        // tri auto avec le critere customComparator defini plus bas:
        Collections.sort(stationsBicloo, new customComparator());

        //On affiche toutes les stations sur la map:
        for (StationBicloo s:stationsBicloo) {
            Jparser parser = new Jparser(this);
            // le parser appelle la fonction addi sur s :
            parser.execute(s);
        }
    }

 /* ================================================= END ONCREATE ===================================== */


    /**
    * Comparateur utilisé pour le calcul des stations les plus proches :
    */
    public class customComparator implements Comparator<StationBicloo> {
        IGeoPoint cen = overlayItemArray.get(0).getPoint();
        @Override
        public int compare(StationBicloo s1, StationBicloo s2) {
            int d1=s1.getLoc().distanceTo(cen);
            int d2=s2.getLoc().distanceTo(cen);
            return d1-d2;
        }
    }

    /**
     *FONCTION POUR AJOUTER UN ITEM A LA MAP A PARTIR D UNE STATION
     * @param s // station bicloo
     */
    public void addicon(StationBicloo s){
        // Choix de l'image de l'icone
        Drawable icon;
        if (s.getNvelos()==0){
            icon=bic_empty;
        }
        else if(s.getNvide()==0){
            icon=bic_full;
        }
        else if(s.getNvelos()<4){
            icon=bic3;
        }
        else {
            icon=bic;
        }
        // fond du rectangle de texte au dessus des icones:
        int or = Color.rgb(255, 160, 0);
        // text icon:
        String aff=Integer.toString(s.getNvelos()) + " / " + Integer.toString(s.getNtot());
        String nome = s.getName();
        double lat = s.getLat().doubleValue();
        double lng = s.getLng().doubleValue();
        GeoPoint gpt = new GeoPoint(lat, lng);
        OverlayItem oi = new OverlayItem(nome, aff, gpt);
        oi.setMarker(icon);
        ArrayList<OverlayItem> liste = new ArrayList<>();
        liste.add(oi);
        // On crée un overlay pour chaque item (= icone), afin de tous les activer en meme temps, pour afficher le rectangle au dessus des items.

        //creation overlay
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(liste, icon, icon, or,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, defaultResourceProxyImpl);
        // sert a activer desactiver au click lorsqu'on a plusieurs items dans un overlay
        mOverlay.setFocusItemsOnTap(true);
        //mOverlay.setFocusedItem(0);
        myOpenMapView.getOverlays().add(mOverlay);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, myLocationListener);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        overridePendingTransition(0, 0); //Permet de ne pas avoir d'animation à la fermeture de l'activité


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(myLocationListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        //On sérialise le fichier menu.xml pour l'afficher dans la barre de menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_carte, menu);

        // Ici on gère le fait qu'on pourra lancer une recherche depuis cette activité
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    //Méthode pour gérer le clic sur les bouttons du menu (sauf la recherche qui est gérée directement par le search manager
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.préférences:
                Intent intent = new Intent(this, ReglagesActivity.class);
                startActivity(intent);
                return true;

            case R.id.favoris:
                Intent intent2 = new Intent(this, MainActivity.class);
                startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void updateLoc(Location loc){
        GeoPoint locGeoPoint = new GeoPoint(loc.getLatitude(), loc.getLongitude());
        setOverlayLoc(loc);

        myOpenMapView.invalidate();
    }

    private void updateCenterLoc(Location loc){
        GeoPoint locGeoPoint = new GeoPoint(loc.getLatitude(), loc.getLongitude());
        myMapController.setCenter(locGeoPoint);
        setOverlayLoc(loc);

        myOpenMapView.invalidate();
    }

    private void setOverlayLoc(Location overlayloc){
        GeoPoint overlocGeoPoint = new GeoPoint(overlayloc);
        //---
        overlayItemArray.clear();

        OverlayItem newMyLocationItem = new OverlayItem(
                "My Location", "My Location", overlocGeoPoint);
        overlayItemArray.add(newMyLocationItem);
        //---
    }

    private LocationListener myLocationListener
            = new LocationListener(){

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            updateLoc(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

    };

    private class MyItemizedIconOverlay extends ItemizedIconOverlay<OverlayItem>{

        public MyItemizedIconOverlay(
                List<OverlayItem> pList,
                org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener<OverlayItem> pOnItemGestureListener,
                ResourceProxy pResourceProxy) {
            super(pList, pOnItemGestureListener, pResourceProxy);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void draw(Canvas canvas, MapView mapview, boolean arg2) {
            // TODO Auto-generated method stub
            super.draw(canvas, mapview, arg2);

            if(!overlayItemArray.isEmpty()){

                //overlayItemArray have only ONE element only, so I hard code to get(0)
                GeoPoint in = (GeoPoint) overlayItemArray.get(0).getPoint();

                Point out = new Point();
                mapview.getProjection().toPixels(in, out);

                Bitmap bm = BitmapFactory.decodeResource(getResources(),
                        R.drawable.mylocation);
                canvas.drawBitmap(bm,
                        out.x - bm.getWidth()/2,  //shift the bitmap center
                        out.y - bm.getHeight()/2,  //shift the bitmap center
                        null);
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event, MapView mapView) {
            // TODO Auto-generated method stub
            //return super.onSingleTapUp(event, mapView);
            return true;
        }
    }
}