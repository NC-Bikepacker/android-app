package ru.netcracker.bikepacker.service;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import ru.netcracker.bikepacker.model.AuthModel;
import ru.netcracker.bikepacker.model.SignUpModel;
import ru.netcracker.bikepacker.model.FriendModel;
import ru.netcracker.bikepacker.model.TrackModel;
import ru.netcracker.bikepacker.model.UserModel;

public interface ApiService {

//    Login and sign up requests
    @Headers({
            "Content-Type: application/json;" +
                    "charset=UTF-8"})
    @POST("/login")
    Call<ru.netcracker.bikepacker.model.UserModel> login(@Body AuthModel authModel);

    @Headers({
            "Content-Type: application/json;" +
                    "charset=UTF-8"})
    @POST("/signup")
    Call<Void> signUp(@Body SignUpModel signUpModel);

//    Post track request
    @POST("/tracks")
    public Call<ResponseBody> postTrack(@Body TrackModel track);

    //operations with usersApi
    @GET("/users/{id}")
    Call<UserModel> getUserWithID(@Path("id") long id);

    @GET("/users/getUser/{userNickName}")
    Call<ru.netcracker.bikepacker.model.UserModel> getFriendWithNickName(@Path("userNickName") String nickName);

    @POST("/users")
    Call<ru.netcracker.bikepacker.model.UserModel> postRequestUser(@Body UserModel findFriendEntity);

    //operations with friendsApi
    @GET("/friends/{nicknameThisUser}")
    Call<List<ru.netcracker.bikepacker.model.UserModel>> getMyFriends(@Path("nicknameThisUser") String nickName);

    @POST("/friends/add")
    Call<ResponseBody> postRequestFriend(@Body FriendModel friends);

    @HTTP(method = "DELETE", path = "/friends/delete", hasBody = true)
    Call<ResponseBody> deleteFriend(@Body FriendModel friends);
}
