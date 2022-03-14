package ru.netcracker.bikepacker.manager;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import ru.netcracker.bikepacker.model.UserModel;

public class UserAccountManager {
   private static UserAccountManager userAccountManager;
   private UserModel user;
   private SessionManager sessionManager;
   private String cookie;

    private UserAccountManager(Context context) throws IOException {
        this.sessionManager = SessionManager.getInstance(context);
        this.user = sessionManager.getSessionUser();
        this.cookie = "JSESSIONID=" + sessionManager.getSessionId() + "; Path=/; HttpOnly;";
    }

    public static UserAccountManager getInstance(Context context) {
        if (userAccountManager == null) {
            try {
                userAccountManager = new UserAccountManager(context);
            } catch (IOException e) {
                Log.d("Error instace UserAccountService", e.getMessage());
            }
        }
        return userAccountManager;
    }

    public UserModel getUser(){
        return user;
    }
    public String getCookie(){
        return cookie;
    }

}
