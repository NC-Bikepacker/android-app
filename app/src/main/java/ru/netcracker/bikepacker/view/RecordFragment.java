package ru.netcracker.bikepacker.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.tracks.TrackRecorder;
import ru.netcracker.bikepacker.tracks.listeners.OnCreatePointListener;
import ru.netcracker.bikepacker.tracks.listeners.OnGpxCreatedListener;
import ru.netcracker.bikepacker.tracks.listeners.OnRecordingEventsListener;
import ru.netcracker.bikepacker.tracks.listeners.OnRecordBtnClickListener;


public class RecordFragment extends Fragment {
    public RecordFragment() {
    }

    public TrackRecorder getTrackRecorder() {
        return trackRecorder;
    }

    private TrackRecorder trackRecorder;
    private Context ctx;
    private boolean recording = false;
    private OnGpxCreatedListener onGpxCreatedListener;
    private OnRecordBtnClickListener onStopBtnClickListener;
    private OnRecordBtnClickListener onStartBtnClickListener;
    private OnCreatePointListener onCreatePointListener;
    private TextView textView;
    private Button btnPoint;

    public void setOnStopBtnClickListener(OnRecordBtnClickListener onStopBtnClickListener) {
        this.onStopBtnClickListener = onStopBtnClickListener;
    }

    public void setBtnPoint(Button btnPoint) {
        this.btnPoint = btnPoint;
    }

    public void setOnCreatePointListener(OnCreatePointListener onCreatePointListener) {
        this.onCreatePointListener = onCreatePointListener;
    }

    public void setOnGpxCreatedListener(OnGpxCreatedListener onGpxCreatedListener) {
        this.onGpxCreatedListener = onGpxCreatedListener;
    }

    public void setOnStartBtnClickListener(OnRecordBtnClickListener onStartBtnClickListener) {
        this.onStartBtnClickListener = onStartBtnClickListener;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ctx = requireContext();

        ImageButton recordButton = view.findViewById(R.id.record);
        LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        textView = view.findViewById(R.id.textView2);
        trackRecorder = new TrackRecorder(ctx, locationManager, textView);
        trackRecorder.setOnRecordingListener(
                new OnRecordingEventsListener() {
                    @Override
                    public void onStartRecording() {
                        view.findViewById(R.id.start_starred_tracks).setVisibility(View.GONE);
                        recordButton.setImageResource(R.drawable.ic_stop_recording);
                        btnPoint.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFinishRecording() {
                        recordButton.setImageResource(R.drawable.ic_start_new_track);
                        onStopBtnClickListener.onClick();
                        onGpxCreatedListener.onGpxCreated(trackRecorder.getGpx());
                        btnPoint.setVisibility(View.INVISIBLE);
                    }
                }
        );
        btnPoint.setOnClickListener(view1 -> onCreatePointListener.onClick());
        recordButton.setOnClickListener(
                view1 -> {
                    if (recording) {
                        trackRecorder.finishRecording();
                    } else {
                        requireActivity().runOnUiThread(
                                new Thread(() -> trackRecorder.startRecording())
                        );
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