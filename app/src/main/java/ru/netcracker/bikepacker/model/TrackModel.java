package ru.netcracker.bikepacker.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Date;


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
    private double trackComplexity;
    @SerializedName("user")
    @Expose
    private UserModel user;
    @SerializedName("gpx")
    @Expose
    private String gpx;
    @SerializedName("imageBase64")
    @Expose
    private String imageBase64;
    @SerializedName("trackName")
    @Expose
    private String trackName;
    @SerializedName("trackDate")
    @Expose
    private Date trackDate;
    @SerializedName("trackDistance")
    @Expose
    private Double trackDistance;
    @SerializedName("trackAvgSpeed")
    @Expose
    private Double trackAvgSpeed;
    @SerializedName("trackStartLat")
    @Expose
    private Double trackStartLat;
    @SerializedName("trackStartLon")
    @Expose
    private Double trackStartLon;
    @SerializedName("trackFinishLat")
    @Expose
    private Double trackFinishLat;
    @SerializedName("trackFinishLon")
    @Expose
    private Double trackFinishLon;

    public TrackModel(long trackId,
                      long travelTime,
                      long trackComplexity,
                      UserModel user,
                      String gpx,
                      String trackName,
                      Date trackDate,
                      Double trackDistance,
                      Double trackAvgSpeed,
                      Double trackStartLat,
                      Double trackStartLon,
                      Double trackFinishLat,
                      Double trackFinishLon
    ) {
        this.travelTime = travelTime;
        this.trackComplexity = trackComplexity;
        this.user = user;
        this.gpx = gpx;
        this.trackId = trackId;
        this.trackName = trackName;
        this.trackDate = trackDate;
        this.trackDistance = trackDistance;
        this.trackAvgSpeed = trackAvgSpeed;
        this.trackStartLat = trackStartLat;
        this.trackStartLon = trackStartLon;
        this.trackFinishLat = trackFinishLat;
        this.trackFinishLon = trackFinishLon;
    }

    public TrackModel(UserModel user) {
        this.user = user;
        this.gpx = "";
    }

    public TrackModel(TrackModel trackModel) {
        this.trackId = trackModel.getTrackId();
        this.travelTime = trackModel.getTravelTime();
        this.trackComplexity = trackModel.getTrackComplexity();
        this.user = trackModel.getUser();
        this.gpx = trackModel.getGpx();
        this.trackName = trackModel.getTrackName();
        this.trackDate = trackModel.getTrackDate();
        this.trackDistance = trackModel.getTrackDistance();
        this.trackAvgSpeed = trackModel.getTrackAvgSpeed();
        this.trackStartLat = trackModel.getTrackStartLat();
        this.trackStartLon = trackModel.getTrackStartLon();
        this.trackFinishLat = trackModel.getTrackFinishLat();
        this.trackFinishLon = trackModel.getTrackFinishLon();
    }

    public TrackModel() {
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public Date getTrackDate() {
        return trackDate;
    }

    public void setTrackDate(Date trackDate) {
        this.trackDate = trackDate;
    }

    public Double getTrackDistance() {
        return trackDistance;
    }

    public void setTrackDistance(Double trackDistance) {
        this.trackDistance = trackDistance;
    }

    public Double getTrackAvgSpeed() {
        return trackAvgSpeed;
    }

    public void setTrackAvgSpeed(Double trackAvgSpeed) {
        this.trackAvgSpeed = trackAvgSpeed;
    }

    public Double getTrackStartLat() {
        return trackStartLat;
    }

    public void setTrackStartLat(Double trackStartLat) {
        this.trackStartLat = trackStartLat;
    }

    public Double getTrackStartLon() {
        return trackStartLon;
    }

    public void setTrackStartLon(Double trackStartLon) {
        this.trackStartLon = trackStartLon;
    }

    public Double getTrackFinishLat() {
        return trackFinishLat;
    }

    public void setTrackFinishLat(Double trackFinishLat) {
        this.trackFinishLat = trackFinishLat;
    }

    public Double getTrackFinishLon() {
        return trackFinishLon;
    }

    public void setTrackFinishLon(Double trackFinishLon) {
        this.trackFinishLon = trackFinishLon;
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

    public double getTrackComplexity() {
        return trackComplexity;
    }

    public void setTrackComplexity(double trackComplexity) {
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

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}