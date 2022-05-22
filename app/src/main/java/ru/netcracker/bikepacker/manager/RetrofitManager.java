package ru.netcracker.bikepacker.manager;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.service.ApiService;

public class RetrofitManager {
    private static volatile RetrofitManager retrofitManager;
    private final Retrofit retrofit;

    private String BASE_URL;
    private Gson gson = new GsonBuilder().setLenient().create();

    private RetrofitManager(Context ctx) throws IOException {
        BASE_URL = ctx.getResources().getString(R.string.server_ip);
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

                if (localInstance == null) {
                    try {
                        retrofitManager = localInstance = new RetrofitManager(ctx);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return retrofitManager;
    }

    public ApiService getJSONApi() {
        return retrofit.create(ApiService.class);
    }
}
