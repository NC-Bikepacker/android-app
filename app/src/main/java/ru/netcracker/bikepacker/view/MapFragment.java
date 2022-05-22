package ru.netcracker.bikepacker.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.ThunderforestTileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.TileSourcePolicy;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.jenetics.jpx.Track;
import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.databinding.FragmentMapBinding;
import ru.netcracker.bikepacker.model.TrackModel;
import ru.netcracker.bikepacker.tracks.GpxUtil;
import ru.netcracker.bikepacker.tracks.UserTrack;

public class MapFragment extends Fragment {
    private MapView map;
    private static final double START_ZOOM = 9.5;
    private static final GeoPoint DEFAULT_POINT = new GeoPoint(51.672, 39.1843);
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private Animation onClickAnim, onClickAnimUp, ActiveOnClickAnim;
    private LocationManager locationManager;
    private IMapController mapController;
    private Context ctx;
    private Drawable startIcon, finishIcon;
    private ImageButton locationBtn, zoomInBtn, zoomOutBtn;
    private GeoPoint userLocation;

    public IMapController getMapController() {
        return mapController;
    }

    public GeoPoint getUserLocation() {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return DEFAULT_POINT;
        }
        @SuppressLint("MissingPermission") Location imHere = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        userLocation = imHere != null ? new GeoPoint(imHere) : DEFAULT_POINT;
        return userLocation;
    }

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

    @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ctx = getContext();
        assert ctx != null;
        startIcon = ctx.getResources().getDrawable(R.drawable.ic_flag_start, ctx.getTheme());
        finishIcon = ctx.getResources().getDrawable(R.drawable.ic_flag_finish, ctx.getTheme());
        onClickAnim = AnimationUtils.loadAnimation(ctx, R.anim.left_trans_repeat);
        onClickAnimUp = AnimationUtils.loadAnimation(ctx, R.anim.up_trans_repeat);
        ActiveOnClickAnim = onClickAnim;

        map = view.findViewById(R.id.map);
        assert ctx != null;
        GpsMyLocationProvider gpsMyLocationProvider = new GpsMyLocationProvider(ctx);

        OnlineTileSourceBase OPENCYCLEMAP = new XYTileSource("Open Cycle Map",
                0, 19, 512, ".png?apikey=063953b0deb84048a549eb28ee778db3", new String[]{
                "https://a.tile.openstreetmap.org/",
                "https://b.tile.openstreetmap.org/",
                "https://c.tile.openstreetmap.org/"}, "© OpenStreetMap contributors",
                new TileSourcePolicy(2,
                        TileSourcePolicy.FLAG_NO_BULK
                                | TileSourcePolicy.FLAG_NO_PREVENTIVE
                                | TileSourcePolicy.FLAG_USER_AGENT_MEANINGFUL
                                | TileSourcePolicy.FLAG_USER_AGENT_NORMALIZED
                ));
        //setting map backdrop
        map.setTileSource(OPENCYCLEMAP);
        //setting visibility for map zoom controllers
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        //setting hand gestures for zoom in/out
        map.setMultiTouchControls(true);

        mapController = map.getController();
        //setting initial zoom
        mapController.setZoom(START_ZOOM);
        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);
        map.getOverlays().add(mRotationGestureOverlay);

        //requesting permissions if they'rnt requested or accepted
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        //location manager set
        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        //setting center on user location
        mapController.setCenter(getUserLocation());

        setupButtons(view, onClickAnim);

        MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);

        Drawable drawable = getResources().getDrawable(R.drawable.ic_directional_arrow);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        locationOverlay.setPersonIcon(bitmap);
        locationOverlay.setDirectionArrow(bitmap, bitmap);
        map.getOverlays().add(locationOverlay);
        map.invalidate();
    }

    public void switchOnClickAnim() {
        if (ActiveOnClickAnim.equals(onClickAnim)) ActiveOnClickAnim = onClickAnimUp;
        else ActiveOnClickAnim = onClickAnim;
        setupButtons(requireView(), ActiveOnClickAnim);
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
        mapController.setCenter(getUserLocation());
    }

    private void setupButtons(@NonNull View view, Animation onClickAnim) {
        locationBtn = view.findViewById(R.id.locationBtn);
        zoomInBtn = view.findViewById(R.id.zoomInBtn);
        zoomOutBtn = view.findViewById(R.id.zoomOutBtn);

        locationBtn.setOnClickListener(
                view1 -> {
                    mapController.animateTo(getUserLocation());
                    view1.startAnimation(onClickAnim);
                }
        );

        zoomInBtn.setOnClickListener(
                view1 -> {
                    mapController.zoomIn();
                    view1.startAnimation(onClickAnim);
                }
        );

        zoomOutBtn.setOnClickListener(
                view1 -> {
                    mapController.zoomOut();
                    view1.startAnimation(onClickAnim);
                }
        );
    }

    public Drawable getStartIcon() {
        return startIcon;
    }

    public Drawable getFinishIcon() {
        return finishIcon;
    }

    public void zoomToTrack(UserTrack userTrack) {
        double ZOOM_VAL = 0.008;
        BoundingBox box = new BoundingBox(
                userTrack.getPolyline().getBounds().getLatNorth() + ZOOM_VAL,
                userTrack.getPolyline().getBounds().getLonEast() + ZOOM_VAL,
                userTrack.getPolyline().getBounds().getLatSouth() - 5 * ZOOM_VAL,
                userTrack.getPolyline().getBounds().getLonWest() - ZOOM_VAL
        );
        if (map.getHeight() > 0) {
            map.setExpectedCenter(new GeoPoint(box.getCenterLatitude(), box.getCenterLongitude()));
            map.zoomToBoundingBox(box, true);
        } else {
            ViewTreeObserver vto = map.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    map.zoomToBoundingBox(box, true);
                    ViewTreeObserver vto2 = map.getViewTreeObserver();
                    vto2.removeOnGlobalLayoutListener(this);
                }
            });
        }
    }
}