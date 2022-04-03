package ru.netcracker.bikepacker.tracks.listeners;

import java.time.Duration;

public interface OnTimeTickedListener {
    void onTick(Duration time);
}
