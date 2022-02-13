package ru.netcracker.bikepacker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;


public class MapFragment extends Fragment {
    protected MapView map;
    protected static final double START_ZOOM = 9.5;
    protected static final GeoPoint DEFAULT_POINT = new GeoPoint(51.672, 39.1843);
    protected static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    protected LocationManager locationManager;
    protected IMapController mapController;
    protected Context ctx;
    private Drawable startIcon, finishIcon;

    public MapView getMap() {
        return map;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ctx = getContext();
        assert ctx != null;
        startIcon = ctx.getResources().getDrawable(R.drawable.ic_flag_start, ctx.getTheme());
        finishIcon = ctx.getResources().getDrawable(R.drawable.ic_flag_finish, ctx.getTheme());

        map = view.findViewById(R.id.map);

        assert ctx != null;
        GpsMyLocationProvider gpsMyLocationProvider = new GpsMyLocationProvider(ctx);

        //setting map backdrop
        map.setTileSource(TileSourceFactory.MAPNIK);
        //setting visibility for map zoom controllers
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        //setting hand gestures for zoom in/out
        map.setMultiTouchControls(true);


        mapController = map.getController();
        //setting initial zoom
        mapController.setZoom(START_ZOOM);

        //requesting permissions if they'rnt requested or accepted
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        //location manager setup
        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        setCenterOnGeoLocation();

        //user location arrow setup
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, map);
        mLocationOverlay.enableMyLocation();
        //TODO: custom user arrow using drawable/ic_user_location_icon.xml (issue in getting drawable:
        // most likely that getting drawable should be in another method)
        map.getOverlays().add(mLocationOverlay);
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

    public void setCenterOnGeoLocation() {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //TODO:возвращение на экран с авторизацией
            return;
        }
        //getting user location
        Location imHere = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        GeoPoint userLocation = imHere != null ? new GeoPoint(imHere) : DEFAULT_POINT;

        //setting center on user location
        mapController.setCenter(userLocation);
        map.invalidate();
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String[] permissionsToRequest = permissions.clone();
        if (permissionsToRequest.length > 0) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissionsToRequest,
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
        setCenterOnGeoLocation();
    }

    public Drawable getStartIcon() {
        return startIcon;
    }

    public Drawable getFinishIcon() {
        return finishIcon;
    }
}