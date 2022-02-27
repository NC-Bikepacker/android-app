package ru.netcracker.bikepacker.network;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;
import ru.netcracker.bikepacker.network.pojos.FriendDTO;
import ru.netcracker.bikepacker.network.pojos.TrackDTO;
import ru.netcracker.bikepacker.network.pojos.UserDTO;

public interface JsonBackendAPI {
    @POST("/tracks")
    public Call<ResponseBody> postTrack(@Body TrackDTO track);

    //operations with usersApi
    @GET("/users/{id}")
    Call<UserDTO> getUserWithID(@Path("id") long id);

    @GET("/users/getUser/{userNickName}")
    Call<UserDTO> getFriendWithNickName(@Path("userNickName") String nickName);

    @POST("/users")
    Call<UserDTO> postRequestUser(@Body UserDTO findFriendEntity);

    //operations with friendsApi
    @GET("/friends/{nicknameThisUser}")
    Call<List<UserDTO>> getMyFriends(@Path("nicknameThisUser") String nickName);

    @POST("/friends/add")
    Call<ResponseBody> postRequestFriend(@Body FriendDTO friends);

    @HTTP(method = "DELETE", path = "/friends/delete", hasBody = true)
    Call<ResponseBody> deleteFriend(@Body FriendDTO friends);
}
