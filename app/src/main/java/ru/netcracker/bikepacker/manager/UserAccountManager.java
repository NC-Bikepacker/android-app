package ru.netcracker.bikepacker.manager;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import ru.netcracker.bikepacker.model.UserModel;

public class UserAccountManager {
   private static volatile UserAccountManager userAccountManager;
   private UserModel user;
   private String cookie;
   private SessionManager sessionManager;

    private UserAccountManager(Context context) throws IOException {
        this.sessionManager = SessionManager.getInstance(context);
        setUserData();
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

    public synchronized void removeUser(){
        this.setUser(null);
        this.setCookie(null);
    }

    public synchronized void setUserData(){
        this.user = sessionManager.getSessionUser();
        this.cookie = "JSESSIONID=" + sessionManager.getSessionId() + "; Path=/; HttpOnly;";
    }

    public UserModel getUser(){
        return user;
    }
    public String getCookie(){
        return cookie;
    }
    private void setUser(UserModel user) { this.user = user;}
    private void setCookie(String cookie) { this.cookie = cookie;}
}
