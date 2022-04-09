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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private final Context ctx;
    private final Handler handler = new Handler();
    private int seconds = 0;
    private double speed = 0;
    private boolean isRecording = false;
    private String timeString;

    //region Constants
    public static final long MIN_TIME_MS = 5000;
    public static final float MIN_DISTANCE_M = 5;
    private static final int UPDATE_TIME_DURATION = 1000;
    //endregion
    //region Objects for track recording
    private final List<WayPoint> wayPoints;
    private final List<Double> speeds;
    private final LocationManager locationManager;
    private final LocationListener recorderListener = new LocationListener() {
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
    };
    //endregion
    //region Objects for making and editing tracks
    private static final GPX.Builder gpxBuilder = GPX.builder();
    private static final Track.Builder trackBuilder = Track.builder();
    private int trackId;
    //endregion
    private final TextView textView;

    public void setOnRecordingListener(OnRecordingEventsListener onRecordingEventsListener) {
        this.onRecordingEventsListener = onRecordingEventsListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public TrackRecorder(Context ctx, LocationManager locationManager, TextView textView) {
        this.ctx = ctx;
        this.locationManager = locationManager;
        this.wayPoints = new ArrayList<>();
        this.speeds = new ArrayList<>();
        this.userAccountManager = UserAccountManager.getInstance(ctx);
        this.textView = textView;
    }

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
            @RequiresApi(api = Build.VERSION_CODES.O)
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
//                Toast.makeText(ctx, String.valueOf(speed), Toast.LENGTH_SHORT).show();
                textView.setText(timeString + ' ' + speedStr);
                seconds++;
                if (isRecording) handler.postDelayed(this, UPDATE_TIME_DURATION);
            }
        });

//        timer.schedule(timeRecorder,0, UPDATE_TIME_DURATION);

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
                    Log.e("PostingTrackError", e.getMessage(), e);
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

    public String getStatistics(StatisticType statisticType) {
        switch (statisticType) {
            case Distanse:
                return String.format(Locale.getDefault(),
                        "%.0f meters",
                        trackBuilder.segments()
                        .stream()
                        .findFirst()
                        .map(TrackSegment::points)
                        .orElse(Stream.empty())
                        .collect(Geoid.WGS84.toPathLength())
                        .to(Length.Unit.METER)
                );
            case Time:
                return timeString;
            default:
                return String.format(Locale.getDefault(),
                        "%.1f Km/h",
                        speeds.stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0)
                );
        }
    }

    public int getSeconds() {
        return seconds;
    }
}