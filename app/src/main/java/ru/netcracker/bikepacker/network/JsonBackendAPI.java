package ru.netcracker.bikepacker.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.netcracker.bikepacker.network.pojos.TrackDTO;

public interface JsonBackendAPI {
    @POST("/tracks")
    public Call<ResponseBody> postTrack(@Body TrackDTO track);
}
