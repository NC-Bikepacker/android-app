package ru.netcracker.bikepacker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;


public class MapFragment extends Fragment {

    private MapView map;
    private static final double START_ZOOM = 9.5;
    private static final GeoPoint DEFAULT_POINT = new GeoPoint(51.672, 39.1843);

    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context ctx = getContext();

        map = view.findViewById(R.id.map);

        assert ctx != null;
        GpsMyLocationProvider gpsMyLocationProvider = new GpsMyLocationProvider(ctx);

        //setting map backdrop
        map.setTileSource(TileSourceFactory.MAPNIK);
        //setting visibility for map zoom controllers
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        //setting hand gestures for zoom in/out
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        //setting initial zoom
        mapController.setZoom(START_ZOOM);

        //location manager setup
        LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //TODO:возвращение на экран с авторизацией
            return;
        }

        //getting user location
        Location imHere = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        GeoPoint userLocation = imHere != null ? new GeoPoint(imHere) : DEFAULT_POINT;

        //setting center on user location
        mapController.setCenter(userLocation);

        //user location arrow setup
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);
        //TODO: custom user arrow using drawable/ic_user_location_icon.xml
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
}