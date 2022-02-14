package ru.netcracker.bikepacker.service;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import ru.netcracker.bikepacker.model.AuthModel;
import ru.netcracker.bikepacker.model.UserModel;

public interface ApiService {

    @Headers({
            "Content-Type: application/json;" +
            "charset=UTF-8"})
    @POST("/login")
    Call<Void> login(@Body AuthModel authModel);

    @GET("/users")
    Call<String> getUserList(String token);

    @Headers({"Accept: application/json"})
    @GET("/users/user/getbyemail/{email}")
    Call<UserModel> getUserByEmail(@Header("Cookie") String cache, @Path("email") String email);
}
