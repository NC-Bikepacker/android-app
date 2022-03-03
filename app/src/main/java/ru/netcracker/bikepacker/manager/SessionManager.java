package ru.netcracker.bikepacker.manager;

import android.content.Context;
import android.content.SharedPreferences;

import ru.netcracker.bikepacker.model.UserModel;

public final class SessionManager {
    private final static String SHARED_PREF = "session";
    private final static String SESSION_ID = "SESSIONID";
    private final static String SESSION_USER_ID = "session_user_id";
    private final static String SESSION_USERNAME = "session_username";
    private final static String SESSION_USER_FIRSTNAME = "session_user_firstname";
    private final static String SESSION_USER_LASTNAME = "session_user_lastname";
    private final static String SESSION_USER_EMAIL = "session_user_email";
    private final static String SESSION_USER_PIC_LINK = "session_user_link";

    private static volatile SessionManager sessionManagerInstance;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static SessionManager getInstance(Context context) {
        SessionManager localInstance = sessionManagerInstance;

        if (localInstance == null) {
            synchronized (SessionManager.class) {
                localInstance = sessionManagerInstance;

                if (localInstance == null) {
                    sessionManagerInstance = localInstance = new SessionManager(context);
                }
            }
        }

        return sessionManagerInstance;
    }

    private SessionManager(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences(context);
            editor = getSharedPreferencesEditor();
        }
    }

    private synchronized SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
    }

    private synchronized SharedPreferences.Editor getSharedPreferencesEditor() {
        return sharedPreferences.edit();
    }

    public synchronized void setSessionId(String sessionId) {
        editor.putString(SESSION_ID, sessionId).commit();
    }

    public synchronized void setSessionUser(UserModel userModel) {
        editor.putString(SESSION_USER_ID, Long.toString(userModel.getId()))
                .putString(SESSION_USERNAME, userModel.getUsername())
                .putString(SESSION_USER_FIRSTNAME, userModel.getFirstname())
                .putString(SESSION_USER_LASTNAME, userModel.getLastname())
                .putString(SESSION_USER_EMAIL, userModel.getEmail())
                .putString(SESSION_USER_PIC_LINK, userModel.getUserPicLink())
                .commit();
    }

    public synchronized UserModel getSessionUser() {
        UserModel userModel = new UserModel();
        userModel.setEmail(getSessionUserEmail());
        userModel.setLastname(getSessionUserLastname());
        userModel.setFirstname(getSessionUserFirstname());
        userModel.setUserPicLink(getSessionUserPicUrl());
        userModel.setUsername(getSessionUsername());
        userModel.setId(getSessionUserId());
        return userModel;
    }

    public synchronized String getSessionId() {
        return sharedPreferences.getString(SESSION_ID, null);
    }

    public synchronized void setSessionUsername(String username) {
        editor.putString(SESSION_USERNAME, username).commit();
    }

    public synchronized String getSessionUsername() {
        return sharedPreferences.getString(SESSION_USERNAME, null);
    }

    public synchronized void setSessionUserFirstname(String firstname) {
        editor.putString(SESSION_USER_FIRSTNAME, firstname).commit();
    }

    public synchronized String getSessionUserFirstname() {
        return sharedPreferences.getString(SESSION_USER_FIRSTNAME, null);
    }

    public synchronized void setSessionUserLastname(String lastname) {
        editor.putString(SESSION_USER_LASTNAME, lastname).commit();
    }

    public synchronized String getSessionUserLastname() {
        return sharedPreferences.getString(SESSION_USER_LASTNAME, null);
    }

    public synchronized void setSessionUserEmail(String email) {
        editor.putString(SESSION_USER_EMAIL, email).commit();
    }

    public synchronized String getSessionUserEmail() {
        return sharedPreferences.getString(SESSION_USER_EMAIL, null);
    }

    public synchronized void setSessionUserPicUrl(String userPicUrl) {
        editor.putString(SESSION_USER_PIC_LINK, userPicUrl).commit();
    }

    public synchronized String getSessionUserPicUrl() {
        return sharedPreferences.getString(SESSION_USER_PIC_LINK, null);
    }

    public synchronized void setSessionUserId(Long id) {
        editor.putLong(SESSION_USER_ID, id).commit();
    }

    public synchronized Long getSessionUserId() {
        return sharedPreferences.getLong(SESSION_USER_ID, -1);
    }

    public synchronized void removeSession() {
        editor.putLong(SESSION_USER_ID, -1)
                .putString(SESSION_USERNAME, null)
                .putString(SESSION_USER_FIRSTNAME, null)
                .putString(SESSION_USER_LASTNAME, null)
                .putString(SESSION_USER_EMAIL, null)
                .putString(SESSION_USER_PIC_LINK, null)
                .putString(SESSION_ID, null)
                .commit();
    }

    public synchronized boolean isEmpty() {
        if (sharedPreferences == null || getSessionId() == null) {
            return true;
        }

        return false;
    }
}
