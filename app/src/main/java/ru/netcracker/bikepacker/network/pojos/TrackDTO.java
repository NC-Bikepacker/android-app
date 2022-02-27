package ru.netcracker.bikepacker.network.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackDTO {

    //    TODO: rename and move

    @SerializedName("track_id")
    @Expose
    private long trackId;
    @SerializedName("travel_time")
    @Expose
    private long travelTime;
    @SerializedName("track_complexity")
    @Expose
    private long trackComplexity;
    @SerializedName("user")
    @Expose
    private UserDTO user;
    @SerializedName("gpx")
    @Expose
    private String gpx;

    public TrackDTO(long travelTime, long trackComplexity, UserDTO user, String gpx) {
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

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String getGpx() {
        return gpx;
    }

    public void setGpx(String gpx) {
        this.gpx = gpx;
    }

}