package ru.netcracker.bikepacker.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Set;

public class PointModel {

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("latitude")
    @Expose
    private double latitude;

    @SerializedName("longitude")
    @Expose
    private double longitude;

    @SerializedName("images")
    @Expose
    private List<String> images;

    @SerializedName("trackId")
    @Expose
    private Long trackId;

    public List<String> getImages() {
        return images;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Long getTrackId() {
        return trackId;
    }

    public PointModel(String description, Long trackId, double latitude, double longitude, List<String> images) {

        this.description = description;
        this.trackId = trackId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.images = images;
    }
}
