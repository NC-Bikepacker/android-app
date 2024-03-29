package ru.netcracker.bikepacker.service;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import ru.netcracker.bikepacker.model.AuthModel;
import ru.netcracker.bikepacker.model.FriendModel;
import ru.netcracker.bikepacker.model.ImageModel;
import ru.netcracker.bikepacker.model.NewsCardModel;
import ru.netcracker.bikepacker.model.PointModel;
import ru.netcracker.bikepacker.model.SignUpModel;
import ru.netcracker.bikepacker.model.TrackModel;
import ru.netcracker.bikepacker.model.UserModel;

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

    @PUT("/updateuserdata/{user_id}")
    Call<UserModel> updateUserData (@Header("Cookie") String cookie, @Path("user_id") Long user_id, @Body SignUpModel signUpModel);

    @GET("/repeatConfirm/{user_id}")
    Call<ResponseBody> repeatConfirm(@Header ("Cookie") String cookie, @Path("user_id") long user_id);

    //Track operations API
    @POST("/tracks")
    Call<ResponseBody> postTrack(@Header("Cookie") String cookie, @Body TrackModel track);

    @GET("/tracks/{id}")
    Call<List<TrackModel>> getTracksByUser(@Header ("Cookie") String cookie, @Path("id") long id);

    @GET("/tracks/getlastfriendtracks/{id}")
    Call<List<TrackModel>> getLastFriendTrack(@Header ("Cookie") String cookie, @Path("id") long id);

    //Put track request
    @PUT("/tracks/update/{id}")
    Call<ResponseBody> putTrack(@Header("Cookie") String cookie, @Path("id") long id, @Body TrackModel track);

    //operations with usersApi
    @GET("/users/user/getbyid/{id}")
    Call<UserModel> getUserWithID(@Header ("Cookie") String cookie, @Path("id") long id);

    @GET("/users/user/getbyusername/{userNickName}")
    Call<UserModel> getFriendWithNickName(@Header ("Cookie") String cookie, @Path("userNickName") String nickName);

    @POST("/users")
    Call<UserModel> postRequestUser(@Header ("Cookie") String cookie, @Body UserModel findFriendEntity);

    //operations with friendsApi
    @GET("/friends/{nicknameThisUser}")
    Call<List<UserModel>> getMyFriends(@Header ("Cookie") String cookie, @Path("nicknameThisUser") String nickName);

    @POST("/friends/add")
    Call<ResponseBody> postRequestFriend(@Header ("Cookie") String cookie, @Body FriendModel friends);

    @HTTP(method = "DELETE", path = "/friends/delete", hasBody = true)
    Call<ResponseBody> deleteFriend(@Header ("Cookie") String cookie, @Body FriendModel friends);

    //FAVORITE TRACKS
    @GET("/favoritetracks/{idThisUser}")
    Call<List<TrackModel>> getMyFavoriteTracks(@Header ("Cookie") String cookie, @Path("idThisUser") Long id);

    //SORTED TRACKS
    @GET("/bydistance/{userid}-{lat}-{lon}")
    Call<List<TrackModel>> getTracksSortedByDistance(@Header ("Cookie") String cookie, @Path("userid") long id,
                                                     @Path("lat") double lat,
                                                     @Path("lon") double lon
    );

    @GET("/bycomplexity/{userid}")
    Call<List<TrackModel>> getTracksSortedByComplexity(@Header ("Cookie") String cookie, @Path("userid") long id);

    @GET("/bytime/{userid}")
    Call<List<TrackModel>> getTracksSortedByTime(@Header ("Cookie") String cookie, @Path("userid") long id);

    //IMAGE
    @GET("/image/track/{id}")
    Call<ImageModel> getTrackImage(@Header ("Cookie") String cookie, @Path("id") Long id);

    @POST("/points/point")
    Call<ResponseBody> addPoint(@Header ("Cookie") String cookie, @Body PointModel point);

    @Multipart
    @POST("/strava/{userId}")
    Call<ResponseBody> postZipFile(@Header ("Cookie") String cookie,
                                   @Path("userId") long userId,
                                   @Part MultipartBody.Part file);

    //NEWS
    @GET("/news/getnews/{userid}")
    Call<List<NewsCardModel>> getNews(@Header ("Cookie") String cookie, @Path("userid") Long userid);

    @POST("/news")
    Call<ResponseBody> postNews(@Header ("Cookie") String cookie, @Body FriendModel friends);

    @DELETE("/news/delete/{id}")
    Call<ResponseBody> deleteNews(@Header ("Cookie") String cookie, @Path("id") Long id);
}