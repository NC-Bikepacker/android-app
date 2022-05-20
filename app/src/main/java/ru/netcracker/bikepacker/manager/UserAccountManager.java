package ru.netcracker.bikepacker.manager;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import ru.netcracker.bikepacker.model.UserModel;

public class UserAccountManager {
   private static volatile UserAccountManager userAccountManager;
   private final UserModel user;
    private final String cookie;

    private UserAccountManager(Context context) throws IOException {
        SessionManager sessionManager = SessionManager.getInstance(context);
        this.user = sessionManager.getSessionUser();
        this.cookie = "JSESSIONID=" + sessionManager.getSessionId() + "; Path=/; HttpOnly;";
    }

    public static UserAccountManager getInstance(Context context) {
        UserAccountManager localUserAccountManager = userAccountManager;
        if (localUserAccountManager == null) {
            try {
                synchronized (UserAccountManager.class){
                    localUserAccountManager = userAccountManager;
                    if(localUserAccountManager ==null){
                        userAccountManager = localUserAccountManager = new UserAccountManager(context);
                    }
                }
            } catch (IOException e) {
                Log.d("Error instance UserAccountService", e.getMessage());
            }
        }
        return localUserAccountManager;
    }

    public UserModel getUser(){
        return user;
    }
    public String getCookie(){
        return cookie;
    }

}
