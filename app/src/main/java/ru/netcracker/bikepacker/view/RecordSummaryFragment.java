package ru.netcracker.bikepacker.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ru.netcracker.bikepacker.databinding.FragmentRecordSummaryBinding;
import ru.netcracker.bikepacker.tracks.StatisticType;
import ru.netcracker.bikepacker.tracks.TrackRecorder;
import ru.netcracker.bikepacker.tracks.listeners.OnAcceptBtnClickListener;

public class RecordSummaryFragment extends Fragment {
    private float ratingVal = 0;

    private FragmentRecordSummaryBinding recordSummaryBinding;
    private Button acceptButton;
    private final TrackRecorder trackRecorder;
    private OnAcceptBtnClickListener onAcceptBtnClickListener;

    public void setOnAcceptBtnClickListener(OnAcceptBtnClickListener onAcceptBtnClickListener) {
        this.onAcceptBtnClickListener = onAcceptBtnClickListener;
    }

    public RecordSummaryFragment(TrackRecorder trackRecorder) {
        this.trackRecorder = trackRecorder;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recordSummaryBinding = FragmentRecordSummaryBinding.inflate(inflater, container, false);

        acceptButton = recordSummaryBinding.acceptButton;
        acceptButton.setVisibility(View.INVISIBLE);
        recordSummaryBinding.complexityRatingBar.setRating(0f);
        recordSummaryBinding.complexityRatingBar.setOnRatingBarChangeListener(
                (ratingBar, v, b) -> {
                    ratingVal = v;
                    recordSummaryBinding.acceptButton.setVisibility(View.VISIBLE);
                }
        );

        recordSummaryBinding.distanse.setText("Distance: "
                + trackRecorder.getStatistics(StatisticType.Distanse)
        );
        recordSummaryBinding.time.setText("Time: "
                + trackRecorder.getStatistics(StatisticType.Time)
        );
        recordSummaryBinding.avgSpeed.setText("Average speed: "
                + trackRecorder.getStatistics(StatisticType.AverageSpeed));

        recordSummaryBinding.acceptButton.setOnClickListener(
                view -> {
                    trackRecorder.sendPutRequest(trackRecorder.getSeconds(),
                            ratingVal
                    );
                    onAcceptBtnClickListener.onClick();
                }
        );


        return recordSummaryBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        recordSummaryBinding.complexityRatingBar.setRating(0f);
        acceptButton.setVisibility(View.INVISIBLE);
    }
}