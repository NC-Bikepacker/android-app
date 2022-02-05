package ru.netcracker.bikepacker.networkService;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.netcracker.bikepacker.R;

public class NetworkService {
    private Context context;
    private static NetworkService mInstance;
    private static final String BASE_URL = "http://192.168.2.2:8085";
    //private static final String BASE_URL = String.valueOf(R.string.server_ip);
    private Retrofit mRetrofit;

    private NetworkService() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static NetworkService getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkService();
        }
        return mInstance;
    }

    public JSONBackendApi getJSONApi(){
        return mRetrofit.create(JSONBackendApi.class);
    }

}