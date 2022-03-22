package ru.netcracker.bikepacker.view;

import android.annotation.SuppressLint;
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

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.tracks.TrackRecorder;
import ru.netcracker.bikepacker.tracks.listeners.OnGpxCreatedListener;
import ru.netcracker.bikepacker.tracks.listeners.OnRecordingEventsListener;
import ru.netcracker.bikepacker.tracks.listeners.OnStopBtnClickListener;


public class RecordFragment extends Fragment {
    public TrackRecorder getTrackRecorder() {
        return trackRecorder;
    }

    private TrackRecorder trackRecorder;
    private Context ctx;
    private boolean recording = false;
    private OnGpxCreatedListener onGpxCreatedListener;
    private OnStopBtnClickListener onStopBtnClickListener;

    public void setOnGpxCreatedListener(OnGpxCreatedListener onGpxCreatedListener) {
        this.onGpxCreatedListener = onGpxCreatedListener;
    }

    public void setOnStopBtnClickListener(OnStopBtnClickListener onStopBtnClickListener) {
        this.onStopBtnClickListener = onStopBtnClickListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ctx = requireContext();

        ImageButton recordButton = view.findViewById(R.id.record);
        LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        trackRecorder = new TrackRecorder(ctx, locationManager);
        trackRecorder.setOnRecordingListener(
            new OnRecordingEventsListener() {
                @Override
                public void onStartRecording() {
                    recordButton.setImageResource(R.drawable.ic_stop_recording);
                }

                @Override
                public void onFinishRecording() {
                    recordButton.setImageResource(R.drawable.ic_start_new_track);
                    onStopBtnClickListener.onClick();
                    onGpxCreatedListener.onGpxCreated(trackRecorder.getGpx());

                }
            }
        );

        recordButton.setOnClickListener(
            view1 -> {
                if (recording) {
                    trackRecorder.finishRecording();
                } else {
                    ((TextView) view.findViewById(R.id.textView2)).setText("Time: ");
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