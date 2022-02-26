package ru.netcracker.bikepacker.networkService;


import android.content.Context;
import android.content.ContextWrapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.zip.Inflater;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.netcracker.bikepacker.R;

public class NetworkService {
    private static NetworkService mInstance;
    private Retrofit mRetrofit;

    private NetworkService(Context context) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        mRetrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.server_ip))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static NetworkService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkService(context);
        }
        return mInstance;
    }

    public JSONBackendApi getJSONApi(){
        return mRetrofit.create(JSONBackendApi.class);
    }

}