package ru.netcracker.bikepacker.tracks;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

public class GpxUtil {
    public static TrackSegment makeSegment(GeoPoint... geoPoints) {
        List<WayPoint> wayPoints = new ArrayList<>();
        for (GeoPoint geoPoint : geoPoints) {
            wayPoints.add(
                    WayPoint.of(
                            geoPoint.getLatitude()
                            , geoPoint.getLongitude()
                            , geoPoint.getAltitude()
                            , 0
                    )
            );
        }
        return TrackSegment.builder().points(wayPoints).build();
    }

    public static TrackSegment makeSegment(WayPoint... wayPoints) {
        return TrackSegment.builder().points(Arrays.asList(wayPoints)).build();
    }

    public static Track makeTrack(TrackSegment... trackSegments) {
        return Track.builder().segments(Arrays.asList(trackSegments)).build();
    }

    public static GPX makeGPX(Track... tracks) {
        return GPX.builder()
                .tracks(Arrays.asList(tracks))
                .build();
    }

    public static Polyline trackToPolyline(Track track) {
        Polyline polyline = new Polyline();
        List<WayPoint> wayPoints = new ArrayList<>();
        track.getSegments().forEach(segment -> wayPoints.addAll(segment.getPoints()));

        polyline.setPoints(wayPointsToGeoPoints(wayPoints));
        return polyline;
    }

    public static List<GeoPoint> wayPointsToGeoPoints(List<WayPoint> wayPoints) {
        List<GeoPoint> geoPointsList = new ArrayList<>();
        wayPoints.forEach(wayPoint ->
                geoPointsList.add(
                        wayPoint.getElevation().isPresent() ?
                                new GeoPoint(
                                        wayPoint.getLatitude().doubleValue(),
                                        wayPoint.getLongitude().doubleValue(),
                                        wayPoint.getElevation().get().doubleValue()
                                ) :
                                new GeoPoint(
                                        wayPoint.getLatitude().doubleValue(),
                                        wayPoint.getLongitude().doubleValue()
                                )
                )
        );
        return geoPointsList;
    }

    public static String gpxToString(GPX gpx) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            GPX.write(gpx,byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }
}
