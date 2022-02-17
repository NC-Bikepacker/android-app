package ru.netcracker.bikepacker.service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import ru.netcracker.bikepacker.model.AuthModel;
import ru.netcracker.bikepacker.model.SignUpModel;
import ru.netcracker.bikepacker.model.UserModel;

public interface ApiService {

    @Headers({
            "Content-Type: application/json;" +
            "charset=UTF-8"})
    @POST("/login")
    Call<UserModel> login(@Body AuthModel authModel);

    @Headers({
            "Content-Type: application/json;" +
            "charset=UTF-8"})
    @POST("/signup")
    Call<Void> signUp(@Body SignUpModel signUpModel);
}
