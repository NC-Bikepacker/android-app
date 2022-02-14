package ru.netcracker.bikepacker.controller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.manager.SessionManager;
import ru.netcracker.bikepacker.model.AuthModel;
import ru.netcracker.bikepacker.service.NetworkService;

public class AuthRequest {

    public static void authReq(Context context, AuthModel authModel) {
        NetworkService.getInstance().getJSONApi().login(authModel).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                Log.e("Error. Authenticated was failed. ", t.getMessage());
                Toast errorToast = Toast.makeText(context, "Error. Authenticated was failed.", Toast.LENGTH_LONG);
                errorToast.show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    Log.d("Message", "Successfully authenticated. Response " + response.code());
                    String sessionId = response.headers().get("Set-Cookie").split("; ")[0].replace("JSESSIONID=", "");
                    SessionManager sessionManager = new SessionManager(context);
                    sessionManager.setSessionId(sessionId);
                } else {
                    Log.d("Message", "Error. Authenticated was failed. Response " + response.code());
                }
            }
        });
    }
}

//TODO:
// колбек контейнер с листом с колбеков

// цепочка обязанностей