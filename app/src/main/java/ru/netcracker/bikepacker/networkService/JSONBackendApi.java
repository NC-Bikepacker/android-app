package ru.netcracker.bikepacker.networkService;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;
import ru.netcracker.bikepacker.model.Friend;
import ru.netcracker.bikepacker.model.User;

public interface JSONBackendApi {

        //operations with usersApi
        @GET("/users/{id}")
        Call<User> getUserWithID(@Path("id") long id);

        @GET("/users/getUser/{userNickName}")
        Call<User> getFriendWithNickName(@Path("userNickName") String nickName);

        @POST("/users")
        Call<User> postRequestUser(@Body User findFriendEntity);

        //operations with friendsApi
        @GET("/friends/{nicknameThisUser}")
        Call<List<User>> getMyFriends(@Path("nicknameThisUser") String nickName);

        @POST("/friends/add")
        Call<ResponseBody> postRequestFriend(@Body Friend friends);

        @HTTP(method = "DELETE", path = "/friends/delete", hasBody = true)
        Call<ResponseBody> deleteFriend(@Body Friend friends);

}
