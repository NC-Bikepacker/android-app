package ru.netcracker.bikepacker.manager;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.service.ApiService;

public class RetrofitManager {
    private static volatile RetrofitManager retrofitManager;
    private final Retrofit retrofit;

    private String BASE_URL;
    private Gson gson = new GsonBuilder().setLenient().create();

    private RetrofitManager(Context ctx) {
        BASE_URL = ctx.getResources().getString(R.string.ip);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static RetrofitManager getInstance(Context ctx) {
        RetrofitManager localInstance = retrofitManager;

        if (localInstance == null) {
            synchronized (RetrofitManager.class) {
                localInstance = retrofitManager;

                if(localInstance == null) {
                    retrofitManager = localInstance = new RetrofitManager(ctx);
                }
            }
        }

        return retrofitManager;
    }

    public ApiService getJSONApi(){
        return retrofit.create(ApiService.class);
    }
}
