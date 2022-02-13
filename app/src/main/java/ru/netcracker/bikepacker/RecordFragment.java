package ru.netcracker.bikepacker;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import io.jenetics.jpx.GPX;
import ru.netcracker.bikepacker.tracks.GpxUtil;
import ru.netcracker.bikepacker.tracks.TrackRecorder;


public class RecordFragment extends Fragment {
    private MapView map;
    private TrackRecorder trackRecorder;
    private Context ctx;
    private boolean recording = false;
    private OnGpxCreatedListener onGpxCreatedListener;
    //    private StartRecordingFragment startRecordingFragment = new StartRecordingFragment();
    private static final String TAG_START = "start";

    //    public RecordFragment(MapView map) {
//        this.map = map;
//    }
    public interface OnGpxCreatedListener {
        void onGpxCreated(GPX gpx);
    }

    public void setOnGpxCreatedListener(OnGpxCreatedListener onGpxCreatedListener) {
        this.onGpxCreatedListener = onGpxCreatedListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    private void setupFragments() {
//        requireActivity().getSupportFragmentManager().beginTransaction()
//                .add(R.id.record_fragment,startRecordingFragment,TAG_START).hide(startRecordingFragment).commit();
//    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
//        Log.d("context",requireContext().toString());
//        Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.popinlayout);
//        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation);
//        container.setLayoutAnimation(layoutAnimationController);
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ctx = requireContext();
//        setupFragments();

        ImageButton recordButton = view.findViewById(R.id.record);
        LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        trackRecorder = new TrackRecorder(ctx, locationManager);
        trackRecorder.setOnRecordingListener(
                new TrackRecorder.OnRecordingEventsListener() {

                    @Override
                    public void onStartRecording() {
                        recordButton.setImageResource(R.drawable.ic_stop_recording);
                    }

                    @Override
                    public void onFinishRecording() {
                        recordButton.setImageResource(R.drawable.ic_start_new_track);
                        Polyline track = GpxUtil.trackToPolyline(trackRecorder.getGpx().getTracks().get(0));
                        onGpxCreatedListener.onGpxCreated(trackRecorder.getGpx());
                    }
                }

        );

        recordButton.setOnClickListener(
                view1 -> {
                    if (recording) {
                        ((TextView) view.findViewById(R.id.textView2)).setText((CharSequence) "Recording finished");
                        view.findViewById(R.id.favourite_tracks).setVisibility(View.VISIBLE);
                        trackRecorder.finishRecording();
                    } else {
                        ((TextView) view.findViewById(R.id.textView2)).setText((CharSequence) "Time: ");
                        view.findViewById(R.id.favourite_tracks).setVisibility(View.GONE);
                        trackRecorder.startRecording();
                    }
                    recording = !recording;
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}