package ru.netcracker.bikepacker.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsCardModel {
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("track")
    @Expose
    private TrackModel track;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("user")
    @Expose
    private UserModel user;
    @SerializedName("images")
    @Expose
    private List<ImageModel> images;
    @SerializedName("likes")
    @Expose
    private Long likes;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public TrackModel getTrack() {
        return track;
    }

    public void setTrack(TrackModel track) {
        this.track = track;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public List<ImageModel> getImages() {
        return images;
    }

    public void setImages(List<ImageModel> images) {
        this.images = images;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public NewsCardModel(long id, TrackModel track, String description, String date, UserModel user, List<ImageModel> images, Long likes) {
        this.id = id;
        this.track = track;
        this.description = description;
        this.date = date;
        this.user = user;
        this.images = images;
        this.likes = likes;
    }
}
