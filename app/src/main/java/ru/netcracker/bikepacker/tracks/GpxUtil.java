package ru.netcracker.bikepacker.tracks;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import ru.netcracker.bikepacker.model.TrackModel;
import ru.netcracker.bikepacker.model.UserModel;

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

    public static Polyline trackModelToPolyline(TrackModel trackModel) throws IOException {
        Polyline polyline = new Polyline();
        Document document = null;
        List<GeoPoint> listPoints = new ArrayList<>();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(new InputSource(new StringReader(trackModel.getGpx())));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        if (document != null){
            NodeList nodeList = document.getElementsByTagName("trkpt");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node lat = nodeList.item(i).getAttributes().getNamedItem("lat");
                Node lon = nodeList.item(i).getAttributes().getNamedItem("lon");
                if (lat != null && lon != null){
                    listPoints.add(new GeoPoint(Double.parseDouble(lat.getNodeValue()), Double.parseDouble(lon.getNodeValue())));
                }
            }
            polyline.setPoints(listPoints);
        }
        return polyline;
    }

    public static String gpxToString(GPX gpx) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            GPX.write(gpx, byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }
}
