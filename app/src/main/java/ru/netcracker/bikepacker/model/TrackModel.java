package ru.netcracker.bikepacker.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TrackModel {

    //    TODO: rename and move

    @SerializedName("trackId")
    @Expose
    private long trackId;
    @SerializedName("travelTime")
    @Expose
    private long travelTime;
    @SerializedName("trackComplexity")
    @Expose
    private long trackComplexity;
    @SerializedName("user")
    @Expose
    private UserModel user;
    @SerializedName("gpx")
    @Expose
    private String gpx;

    public TrackModel(long travelTime, long trackComplexity, UserModel user, String gpx) {
        this.travelTime = travelTime;
        this.trackComplexity = trackComplexity;
        this.user = user;
        this.gpx = gpx;
    }

    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    public long getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(long travelTime) {
        this.travelTime = travelTime;
    }

    public long getTrackComplexity() {
        return trackComplexity;
    }

    public void setTrackComplexity(long trackComplexity) {
        this.trackComplexity = trackComplexity;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getGpx() {
        return gpx;
    }

    public void setGpx(String gpx) {
        this.gpx = gpx;
    }
}