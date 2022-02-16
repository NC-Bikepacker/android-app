package ru.netcracker.bikepacker.tracks.listeners;

import io.jenetics.jpx.GPX;

public interface OnGpxCreatedListener {
    void onGpxCreated(GPX gpx);
}