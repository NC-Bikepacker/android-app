package ru.netcracker.bikepacker.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class AuthModel {
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;

    public AuthModel(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public AuthModel() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthModel authModel = (AuthModel) o;
        return Objects.equals(email, authModel.email) && Objects.equals(password, authModel.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }

    @NonNull
    @Override
    public String toString() {
        return "{\"email\":\"" + email + "\"," +
                "\"password\":\"" + password + "\"}";
    }
}
