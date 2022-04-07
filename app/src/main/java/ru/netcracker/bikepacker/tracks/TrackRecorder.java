package ru.netcracker.bikepacker.tracks;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.manager.RetrofitManager;
import ru.netcracker.bikepacker.manager.UserAccountManager;
import ru.netcracker.bikepacker.model.TrackModel;
import ru.netcracker.bikepacker.tracks.listeners.OnRecordingEventsListener;

public class TrackRecorder {
    private OnRecordingEventsListener onRecordingEventsListener;

    private final LocationManager locationManager;
    private final List<WayPoint> wayPoints;
    private final List<WayPoint> points;
    private final Context ctx;
    public static final long MIN_TIME_MS = 5000;
    public static final float MIN_DISTANCE_M = 5;
    private final UserAccountManager userAccountManager;
    private final Timer timer = new Timer();
    private final LocationListener recorderListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            int ind = wayPoints.size() - 1;
            double lat = location.getLatitude();
            double lon = location.getLongitude();

            WayPoint wp = WayPoint.builder()
                    .lat(lat)
                    .lon(lon)
                    .build();
            wayPoints.add(wp);
            Toast.makeText(ctx, "changed", Toast.LENGTH_SHORT).show();
        }
    };
    private static final GPX.Builder gpxBuilder = GPX.builder();
    private static final Track.Builder trackBuilder = Track.builder();

    public int getTrackId() {
        return trackId;
    }

    private int trackId;
    private static final int UPDATE_TIME_DURATION = 1000;

    public void setOnRecordingListener(OnRecordingEventsListener onRecordingEventsListener) {
        this.onRecordingEventsListener = onRecordingEventsListener;
    }

    public GeoPoint getLastLocation() {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (userLocation == null) {
            Toast.makeText(ctx,"Recording start failed",Toast.LENGTH_LONG).show();
            return null;
        }
        return new GeoPoint(userLocation);
    }
    public void addPoint(String description) {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (userLocation == null) {
            Toast.makeText(ctx,"Recording start failed",Toast.LENGTH_LONG).show();
            return;
        }
        GeoPoint start = new GeoPoint(userLocation);
        points.add(WayPoint.builder().lat(start.getLatitude()).lon(start.getLongitude()).desc(description).build());
    }

    public TrackRecorder(Context ctx, LocationManager locationManager) {
        this.ctx = ctx;
        this.locationManager = locationManager;
        this.wayPoints = new ArrayList<>();
        this.userAccountManager = UserAccountManager.getInstance(ctx);
        this.points = new ArrayList<>();
    }

    public void startRecording() {
        wayPoints.clear();
        trackBuilder.segments().clear();
        gpxBuilder.tracks().clear();
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_MS,
                MIN_DISTANCE_M,
                recorderListener);
        Location userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (userLocation == null) {
            Toast.makeText(ctx, "Recording start failed", Toast.LENGTH_LONG).show();
            return;
        }
        GeoPoint start = new GeoPoint(userLocation);
        wayPoints.add(WayPoint.builder().lat(start.getLatitude()).lon(start.getLongitude()).build());
        sendPostRequest();
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
        sendPutRequest();
        onRecordingEventsListener.onFinishRecording();
    }

    public GPX getGpx() {
        return gpxBuilder
                .addTrack(
                        trackBuilder.build()
                ).build();
    }

    // generally used geo measurement function
    public static double measure(double lat1, double lon1, double lat2, double lon2) {
        double R = 6378.137; // Radius of earth in KM
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d * 1000; // meters
    }

    public void sendPostRequest() {
        TrackModel trackToPost = new TrackModel(userAccountManager.getUser());
        RetrofitManager.getInstance(ctx).getJSONApi().postTrack(userAccountManager.getCookie(), trackToPost).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String temp;
                        assert response.body() != null;
                        temp = response.body().string();
                        Log.d("CALLBACK", temp);

                        Matcher matcher = Pattern.compile("(\"trackId\":)(\\d+)")
                                .matcher(temp.replaceAll("\\p{C}", "").trim());
                        if (matcher.find()) {
                            temp = matcher.group(2);
                            assert temp != null;
                            trackId = Integer.parseInt(temp);

                            Log.d("TrackPostingCallback", "TRACK №" + trackId + " SENT");
                        } else {
                            Log.e("IpNotFoundError", "IP parsing failed");
                        }

                    } else {
                        Log.e("PostingTrackError", String.format("Error response: %d %s", response.code(), response.message()));
                        ResponseBody errorBody = response.errorBody();
                        if (errorBody != null) {
                            Log.e("PostingTrackError", "Error body:\n" + errorBody.string());
                        }
                    }

                } catch (IOException e) {
                    Log.e("PostingTrackError",e.getMessage(),e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Track sending callback", "NOT SENT");
            }
        });
    }

    public void sendPutRequest(long travelTime, float complexity) {
        //TODO: Change complexity to float
        TrackModel trackToPut = new TrackModel(travelTime, (long) complexity, userAccountManager.getUser(), GpxUtil.gpxToString(getGpx()));

        RetrofitManager.getInstance(ctx).getJSONApi()
                .putTrack(userAccountManager.getCookie(), trackId, trackToPut)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.d("TrackPuttingCallback", "PUT");
                        } else {
                            Log.e("PuttingTrackError", String.format("Error response: %d %s", response.code(), response.message()));
                            ResponseBody errorBody = response.errorBody();
                            if (errorBody != null) {
                                try {
                                    Log.e("PuttingTrackError", "Error body:\n" + errorBody.string());
                                } catch (IOException e) {
                                    Log.e("ErrorBodyStringError", e.getMessage(), e);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("TrackPuttingCallback", "NOT PUT");
                    }
                });
    }

    public void sendPutRequest() {
        TrackModel trackToPut = new TrackModel(userAccountManager.getUser());
        trackToPut.setGpx(GpxUtil.gpxToString(getGpx()));
        RetrofitManager.getInstance(ctx).getJSONApi()
                .putTrack(userAccountManager.getCookie(), trackId, trackToPut)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.d("TrackPuttingCallback", "PUT");
                        } else {
                            Log.e("PuttingTrackError", String.format("Error response: %d %s", response.code(), response.message()));
                            ResponseBody errorBody = response.errorBody();
                            if (errorBody != null) {
                                try {
                                    Log.e("PuttingTrackError", "Error body:\n" + errorBody.string());
                                } catch (IOException e) {
                                    Log.e("ErrorBodyStringError", e.getMessage(), e);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("TrackPuttingCallback", "NOT PUT");
                    }
                });
    }
    public List<WayPoint> getPoints() {
        return points;
    }
}