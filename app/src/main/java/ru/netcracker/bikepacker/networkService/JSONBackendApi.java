package ru.netcracker.bikepacker.networkService;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import ru.netcracker.bikepacker.model.Friends;
import ru.netcracker.bikepacker.model.User;

public interface JSONBackendApi {

        //operations with users
        @GET("/users/{id}")
        Call<User> getFriendWithID(@Path("id") int id);

        @GET("/users/getUser/{userNickName}")
        Call<User> getFriendWithNickName(@Path("userNickName") String nickName);

        @POST("/users")
        Call<User> postRequestUser(@Body User findFriendEntity);

        //operations with friends
        @GET("/friends/{nicknameThisUser}")
        Call<List<User>> getMyFriends(@Path("nicknameThisUser") String nickName);

        @POST("/friends/add")
        Call<RequestBody> postRequestFriend(@Body Friends friends);

}
