package ru.netcracker.bikepacker.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class UserModel implements Serializable {

    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("userPicLink")
    @Expose
    private String userPicLink;
    @SerializedName("id")
    @Expose
    private Long id;

    public UserModel() {
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getUserPicLink() {
        return userPicLink;
    }

    public Long getId() {
        return id;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserPicLink(String userPicLink) {
        this.userPicLink = userPicLink;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserModel)) return false;
        UserModel userModel = (UserModel) o;
        return Objects.equals(getFirstname(), userModel.getFirstname()) && Objects.equals(getLastname(), userModel.getLastname()) && Objects.equals(getEmail(), userModel.getEmail()) && Objects.equals(getUsername(), userModel.getUsername()) && Objects.equals(getUserPicLink(), userModel.getUserPicLink()) && Objects.equals(getId(), userModel.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstname(), getLastname(), getEmail(), getUsername(), getUserPicLink(), getId());
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", userPicLink='" + userPicLink + '\'' +
                ", id=" + id +
                '}';
    }
}
