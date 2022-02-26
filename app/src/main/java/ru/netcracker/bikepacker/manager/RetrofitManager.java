package ru.netcracker.bikepacker.manager;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.service.ApiService;

public class RetrofitManager {
    private static RetrofitManager mInstance;
    private final Retrofit mRetrofit;

    private static String BASE_URL;
    private static Gson gson = new GsonBuilder().setLenient().create();

    private RetrofitManager(Context ctx) {
        BASE_URL = ctx.getResources().getString(R.string.ip);
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static RetrofitManager getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new RetrofitManager(ctx);
        }
        return mInstance;
    }

    public ApiService getJSONApi(){
        return mRetrofit.create(ApiService.class);
    }

}
