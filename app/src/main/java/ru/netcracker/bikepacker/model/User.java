package ru.netcracker.bikepacker.model;

import com.google.gson.annotations.*;

public class User {

    @SerializedName("id")
    @Expose
    Long id;

    @SerializedName("firstname")
    @Expose
    private String firstName;

    @SerializedName("lastname")
    @Expose
    private String lastName;

    @SerializedName("nickname")
    @Expose
    private String nickName;

    @SerializedName("avatarImageUrl")
    @Expose
    private String userPic_url;
    
    

    public User(Long id, String firstName, String lastName, String nickName, String userPic_url) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = nickName;
        this.userPic_url = userPic_url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserPic_url() {
        return userPic_url;
    }

    public void setUserPic_url(String userPic_url) {
        this.userPic_url = userPic_url;
    }

}
