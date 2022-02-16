package ru.netcracker.bikepacker.tracks;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayWithIW;
import org.osmdroid.views.overlay.Polyline;

import java.util.Arrays;
import java.util.List;

public class UserTrack extends OverlayWithIW {
    public static final String RECORDED_TRACK_TAG = "rec";
    private static final Paint RECORDED_TRACK_PAINT = new Paint();
    private static final float ANC_U = Marker.ANCHOR_LEFT, ANC_V = Marker.ANCHOR_BOTTOM;
    private static final double ZOOM_VAL = 0.0005;

    private final Polyline polyline;
    private final Marker startMarker;
    private final Marker finishMarker;

    UserTrack trackBuilder;

    private UserTrack(MapView map, Drawable startIcon, Drawable finishIcon, Polyline polyline) {
        RECORDED_TRACK_PAINT.setARGB(255, 93, 62, 168);
        RECORDED_TRACK_PAINT.setStrokeWidth(10F);
        RECORDED_TRACK_PAINT.setStrokeCap(Paint.Cap.ROUND);

        polyline.getOutlinePaint().set(RECORDED_TRACK_PAINT);
        polyline.setSubDescription(RECORDED_TRACK_TAG);
        polyline.setId(RECORDED_TRACK_TAG);
        this.polyline = polyline;

        startMarker = new Marker(map);
        startMarker.setAnchor(ANC_U,ANC_V);
        startMarker.setIcon(startIcon);
        startMarker.setSubDescription(RECORDED_TRACK_TAG);
        startMarker.setPosition(
                polyline.getActualPoints()
                        .get(0)
        );
        startMarker.setId(RECORDED_TRACK_TAG);

        finishMarker = new Marker(map);
        finishMarker.setAnchor(ANC_U,ANC_V);
        finishMarker.setIcon(finishIcon);
        finishMarker.setSubDescription(RECORDED_TRACK_TAG);
        finishMarker.setPosition(
                polyline.getActualPoints()
                .get(polyline.getActualPoints().size() - 1)
        );
        finishMarker.setId(RECORDED_TRACK_TAG);
    }

    public static UserTrack newInstance(MapView map, Drawable startIcon, Drawable finishIcon, Polyline polyline
    ) {
        return new UserTrack(map,startIcon,finishIcon,polyline);
    }

    public List<OverlayWithIW> toList() {
        return Arrays.asList(this.polyline, this.startMarker, this.finishMarker);
    }

    public BoundingBox getBoundingBox() {
        return new BoundingBox(
                polyline.getBounds().getLatNorth() + ZOOM_VAL,
                polyline.getBounds().getLonEast() + ZOOM_VAL,
                polyline.getBounds().getLatSouth() - ZOOM_VAL,
                polyline.getBounds().getLonWest() - ZOOM_VAL
        );
    }
}
