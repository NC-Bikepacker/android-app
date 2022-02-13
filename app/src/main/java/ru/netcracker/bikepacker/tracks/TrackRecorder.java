package ru.netcracker.bikepacker.tracks;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.network.NetworkService;
import ru.netcracker.bikepacker.network.pojos.TrackDTO;
import ru.netcracker.bikepacker.network.pojos.UserDTO;


public class TrackRecorder {
    private OnRecordingEventsListener onRecordingEventsListener;
    private final LocationManager locationManager;
    private final List<WayPoint> wayPoints;
    private final Context ctx;
    private static final long MIN_TIME_MS = 5000;
    private static final float MIN_DISTANCE_M = 5;
    private final LocationListener recorderListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            wayPoints.add(WayPoint.of(
                    location.getLatitude()
                    , location.getLongitude()
                    , location.getAltitude()
                    , location.getTime()));
            Toast.makeText(ctx, "changed", Toast.LENGTH_SHORT).show();
        }
    };
    private static final GPX.Builder gpxBuilder = GPX.builder();
    private static final Track.Builder trackBuilder = Track.builder();

    public void setOnRecordingListener(OnRecordingEventsListener onRecordingEventsListener) {
        this.onRecordingEventsListener = onRecordingEventsListener;
    }

    public interface OnRecordingEventsListener {
        void onStartRecording();

        void onFinishRecording();
    }

    public TrackRecorder(Context ctx, LocationManager locationManager) {
        this.ctx = ctx;
        this.locationManager = locationManager;
        this.wayPoints = new ArrayList<>();
    }

    public void startRecording() {
        wayPoints.clear();

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        this.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_MS,
                MIN_DISTANCE_M,
                recorderListener);

        Location userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (userLocation == null) return;
        GeoPoint start = new GeoPoint(userLocation);
        wayPoints.add(WayPoint.of(start.getLatitude(), start.getLongitude()));

        onRecordingEventsListener.onStartRecording();
    }

    public void finishRecording() {
        this.locationManager.removeUpdates(recorderListener);
        trackBuilder.addSegment(
                TrackSegment.builder()
                        .points(wayPoints)
                        .build()
        );
        wayPoints.clear();

        UserDTO user = new UserDTO(1L);
        TrackDTO trackToPost = new TrackDTO(2, 3, user, GpxUtil.gpxToString(getGpx()));

        NetworkService.getInstance().getJsonBackendAPI().postTrack(trackToPost).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {}

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
        onRecordingEventsListener.onFinishRecording();
    }

    public GPX getGpx() {
        return gpxBuilder
                .addTrack(
                        trackBuilder.build()
                ).build();
    }

}