package ru.netcracker.bikepacker.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageModel {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("imageBase64")
    @Expose
    private String imageBase64;
    @SerializedName("user")
    @Expose
    private UserModel user;
    @SerializedName("track")
    @Expose
    private TrackModel track;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public Object getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public TrackModel getTrack() {
        return track;
    }

    public void setTrack(TrackModel track) {
        this.track = track;
    }

}
