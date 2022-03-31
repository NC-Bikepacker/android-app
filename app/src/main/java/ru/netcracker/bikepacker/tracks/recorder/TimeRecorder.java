package ru.netcracker.bikepacker.tracks.recorder;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Duration;
import java.util.TimerTask;

import ru.netcracker.bikepacker.tracks.listeners.OnTimeTickedListener;

@RequiresApi(api = Build.VERSION_CODES.O)
public class TimeRecorder extends TimerTask {
    private final Duration timerTime = Duration.ZERO;
    private final int duration;
    private OnTimeTickedListener onTimeTickedListener;

    public TimeRecorder(int duration, OnTimeTickedListener onTimeTickedListener) {
        this.duration = duration;
        this.onTimeTickedListener = onTimeTickedListener;
    }

    @Override
    public void run() {
        timerTime.plusMillis(duration);
        onTimeTickedListener.onTick(timerTime);
    }

    public void removeListener() {
        onTimeTickedListener = time -> {
        };
    }

    public void setOnTimeTickedListener(OnTimeTickedListener onTimeTickedListener) {
        this.onTimeTickedListener = onTimeTickedListener;
    }
}
