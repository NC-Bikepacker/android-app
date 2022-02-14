package ru.netcracker.bikepacker.controller;

import android.content.Context;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.manager.SessionManager;
import ru.netcracker.bikepacker.model.UserModel;
import ru.netcracker.bikepacker.service.NetworkService;

public class UsersRequest {

    private static UserModel getUser(Context context, String email) {
        SessionManager sessionManager = new SessionManager(context);
        String sessionId = sessionManager.getSessionId();
        UserModel[] userModel = {new UserModel()};

        NetworkService.getInstance().getJSONApi().getUserByEmail("JSESSIONID=" + sessionId, email).enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful()) {
                    userModel[0] = response.body();
                } else {
                    Log.d("Error", "Getting user by email was failed. Response code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Log.d("Error", "Getting user by email was failed. Response code:\n" + t.getMessage());
            }
        });

        return userModel[0];
    }
}
