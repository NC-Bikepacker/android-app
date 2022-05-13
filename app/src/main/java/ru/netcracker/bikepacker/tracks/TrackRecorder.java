package ru.netcracker.bikepacker.tracks;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Speed;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.geom.Geoid;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.manager.RetrofitManager;
import ru.netcracker.bikepacker.manager.UserAccountManager;
import ru.netcracker.bikepacker.model.TrackModel;
import ru.netcracker.bikepacker.tracks.listeners.OnRecordingEventsListener;


public class TrackRecorder {
    private final UserAccountManager userAccountManager;
    private OnRecordingEventsListener onRecordingEventsListener;
    private final LocationManager locationManager;
    private final List<WayPoint> wayPoints;
    private final List<WayPoint> points;
    private final Context ctx;
    private final Handler handler = new Handler();
    private int seconds = 0;
    private double speed = 0;
    private boolean isRecording = false;
    private String timeString;
    private TrackModel trackToSend;
    //region Constants
    public static final long MIN_TIME_MS = 5000;
    public static final float MIN_DISTANCE_M = 5;
    private static final int UPDATE_TIME_DURATION = 1000;
    //endregion
    //region Objects for track recording
    private final List<Double> speeds;
    private final RecordingLocationListener recorderListener = new RecordingLocationListener();
    //endregion
    //region Objects for making and editing tracks
    private static final GPX.Builder gpxBuilder = GPX.builder();
    private static final Track.Builder trackBuilder = Track.builder();
    private Long trackId;
    //endregion
    private final TextView textView;

    public Long getTrackId() {
        return trackId;
    }

    public void setOnRecordingListener(OnRecordingEventsListener onRecordingEventsListener) {
        this.onRecordingEventsListener = onRecordingEventsListener;
    }

    public Optional<GeoPoint> getLastLocation() {

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return Optional.empty();
        }
        Location userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (userLocation == null) {
            Toast.makeText(ctx, "No last location found", Toast.LENGTH_LONG).show();
            return Optional.empty();
        }
        return Optional.of(new GeoPoint(userLocation));
    }

    public void addPoint(String description) {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (userLocation == null) {
            Toast.makeText(ctx, "Recording start failed", Toast.LENGTH_LONG).show();
            return;
        }
        GeoPoint start = new GeoPoint(userLocation);
        points.add(WayPoint.builder().lat(start.getLatitude()).lon(start.getLongitude()).desc(description).build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public TrackRecorder(Context ctx, LocationManager locationManager, TextView textView) {
        this.ctx = ctx;
        this.locationManager = locationManager;
        this.wayPoints = new ArrayList<>();
        this.speeds = new ArrayList<>();
        this.userAccountManager = UserAccountManager.getInstance(ctx);
        this.points = new ArrayList<>();
        this.textView = textView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startRecording() {
        seconds = 0;
        isRecording = true;
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

        handler.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                timeString = String.format(Locale.getDefault(),
                        "%02d:%02d",
                        minutes, secs);
                if (hours >= 1) timeString = hours + ":" + timeString;

                String speedStr = String.format(Locale.getDefault(), "%.1f", speed) + " Km/h";
                textView.setText(timeString + ' ' + speedStr);
                seconds++;
                if (isRecording) handler.postDelayed(this, UPDATE_TIME_DURATION);
            }
        });

        wayPoints.add(WayPoint.builder().lat(start.getLatitude()).lon(start.getLongitude()).build());
        sendPostRequest();
        onRecordingEventsListener.onStartRecording();
    }

    public void finishRecording() {
        isRecording = false;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendPostRequest() {
        trackToSend = new TrackModel(userAccountManager.getUser());
        trackToSend.setTrackDate(Date.valueOf(LocalDate.now().toString()));

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String partOfTheDay;
        if (4 <= hour && hour < 12) partOfTheDay = "Morning";
        else if (12 <= hour && hour < 15) partOfTheDay = "Noon";
        else if (15 <= hour && hour < 21) partOfTheDay = "Evening";
        else if (21 <= hour) partOfTheDay = "Night";
        else partOfTheDay = "Night";
        partOfTheDay += " trip";
        trackToSend.setTrackName(partOfTheDay);

        wayPoints.stream().findFirst().ifPresent(wayPoint -> {
            trackToSend.setTrackStartLat(wayPoint.getLatitude().doubleValue());
            trackToSend.setTrackStartLon(wayPoint.getLongitude().doubleValue());
        });
        RetrofitManager.getInstance(ctx).getJSONApi().postTrack(userAccountManager.getCookie(), trackToSend).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String temp = null;
                        assert response.body() != null;
                        temp = response.body().string();
                        Log.d("CALLBACK", temp);
                        trackId = Long.parseLong(temp);
                        Log.d("TrackPostingCallback", "TRACK №" + trackId + " SENT");

                    } else {
                        Log.e("PostingTrackError", String.format("Error response: %d %s", response.code(), response.message()));
                        ResponseBody errorBody = response.errorBody();
                        if (errorBody != null) {
                            Log.e("PostingTrackError", "Error body:\n" + errorBody.string());
                        }
                    }

                } catch (IOException e) {
                    Log.e("PostingTrackError", e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("Track sending callback", "error send response. Error message: " + t.getMessage(), t);
            }
        });
    }

    public void sendPutRequest(long travelTime, double complexity) {
        trackToSend.setTravelTime(travelTime);
        trackToSend.setTrackComplexity(complexity);

        RetrofitManager.getInstance(ctx).getJSONApi()
                .putTrack(userAccountManager.getCookie(), trackId, trackToSend)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
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
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Log.e("TrackPuttingCallback", "error put response. Error message: " + t.getMessage(), t);
                    }
                });
    }

    public void sendPutRequest() {
        trackToSend.setTrackId(trackId);

        GPX gpx = getGpx();
        trackToSend.setGpx(GpxUtil.gpxToString(gpx));

        if (trackToSend.getTrackStartLat() == null || trackToSend.getTrackStartLon() == null) {
            gpx.tracks()
                    .findFirst()
                    .flatMap(
                            trackSegments ->
                                    trackSegments.segments()
                                            .findFirst()
                                            .flatMap(
                                                    wayPoints -> wayPoints.points()
                                                            .findFirst()
                                            )
                    ).ifPresent(wayPoint -> {
                        trackToSend.setTrackStartLat(wayPoint.getLatitude().doubleValue());
                        trackToSend.setTrackStartLon(wayPoint.getLongitude().doubleValue());
                    });
        }
        gpx.tracks().findFirst().flatMap(trackSegments -> trackSegments.segments().findFirst()).ifPresent(
                wayPoints -> {
                    List<WayPoint> points = wayPoints.getPoints();
                    WayPoint last = points.get(points.size() - 1);
                    trackToSend.setTrackFinishLat(last.getLatitude().doubleValue());
                    trackToSend.setTrackFinishLon(last.getLongitude().doubleValue());
                }
        );

        trackToSend.setTrackDistance(getStatisticDouble(StatisticType.Distance));
        trackToSend.setTrackAvgSpeed(getStatisticDouble(StatisticType.AverageSpeed));

        RetrofitManager.getInstance(ctx).getJSONApi()
                .putTrack(userAccountManager.getCookie(), trackId, trackToSend)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
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
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Log.e("TrackPuttingCallback", "error put response. Error message: " + t.getMessage(), t);
                    }
                });
    }

    public List<WayPoint> getPoints() {
        return points;
    }

    public String getStatisticString(StatisticType statisticType) {
        switch (statisticType) {
            case Distance:
                return String.format(Locale.getDefault(),
                        "%.0f meters",
                        getStatisticDouble(StatisticType.Distance)
                );
            case Time:
                return timeString;
            default:
                return String.format(Locale.getDefault(),
                        "%.1f Km/h",
                        getStatisticDouble(StatisticType.AverageSpeed)
                );
        }
    }

    public double getStatisticDouble(StatisticType statisticType) {
        switch (statisticType) {
            case Distance:
                return trackBuilder.segments()
                        .stream()
                        .findFirst()
                        .map(TrackSegment::points)
                        .orElse(Stream.empty())
                        .collect(Geoid.WGS84.toPathLength())
                        .to(Length.Unit.METER);
            case Time:
                return seconds;
            default:
                return speeds.stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0);
        }
    }

    public int getSeconds() {
        return seconds;
    }

    class RecordingLocationListener implements LocationListener {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onLocationChanged(@NonNull Location location) {
            WayPoint prev = wayPoints.get(wayPoints.size() - 1);
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            int actualTime = seconds;

            WayPoint currentPoint = WayPoint.builder()
                    .lat(lat)
                    .lon(lon)
                    .time(actualTime)
                    .build();

            double len = Geoid.WGS84.distance(currentPoint, prev).doubleValue();
            AtomicInteger time = new AtomicInteger();
            prev.getTime().ifPresent(zonedDateTime -> time.set(actualTime - zonedDateTime.toLocalTime().toSecondOfDay()));

            double countedSpeed = len / time.doubleValue() * 36 / 10;
            speed = Double.isInfinite(countedSpeed) || Double.isNaN(countedSpeed) ? speed : countedSpeed;
            currentPoint = currentPoint.toBuilder().speed(Speed.of(speed, Speed.Unit.KILOMETERS_PER_HOUR)).build();

            if (speed != 0) speeds.add(speed);
            wayPoints.add(currentPoint);
        }
    }
}