package ru.netcracker.bikepacker.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import ru.netcracker.bikepacker.model.AuthModel;
import ru.netcracker.bikepacker.model.SignUpModel;
import ru.netcracker.bikepacker.model.UserModel;
import ru.netcracker.bikepacker.network.pojos.TrackDTO;

public interface ApiService {

//    Login and sign up requests
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

//    Post track request
    @POST("/tracks")
    public Call<ResponseBody> postTrack(@Body TrackDTO track);
}
