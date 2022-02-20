package ru.netcracker.bikepacker.network;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.netcracker.bikepacker.R;

public class NetworkService {
    private static NetworkService mInstance;
    private final Retrofit mRetrofit;

    private NetworkService(Context ctx) {
        String baseUrl = ctx.getResources().getString(R.string.ip);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static NetworkService getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new NetworkService(ctx);
        }
        return mInstance;
    }

    public JsonBackendAPI getJsonBackendAPI(){
        return mRetrofit.create(JsonBackendAPI.class);
    }

}
