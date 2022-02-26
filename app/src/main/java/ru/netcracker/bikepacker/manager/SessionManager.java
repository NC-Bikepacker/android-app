package ru.netcracker.bikepacker.manager;

import android.content.Context;
import android.content.SharedPreferences;

import ru.netcracker.bikepacker.model.UserModel;

public class SessionManager {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private final String SHARED_PREF = "session";
    private final String SESSION_ID = "SESSIONID";
    private final String SESSION_USER_ID = "session_user_id";
    private final String SESSION_USERNAME = "session_username";
    private final String SESSION_USER_FIRSTNAME = "session_user_firstname";
    private final String SESSION_USER_LASTNAME = "session_user_lastname";
    private final String SESSION_USER_EMAIL = "session_user_email";
    private final String SESSION_USER_PIC_LINK = "session_user_link";

    public SessionManager(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences(context);
            editor = getSharedPreferencesEditor();
        }
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getSharedPreferencesEditor() {
        return sharedPreferences.edit();
    }

    public void setSessionId(String sessionId) {
        editor.putString(SESSION_ID, sessionId).commit();
    }

    public void setSessionUser(UserModel userModel) {
        editor.putString(SESSION_USER_ID, Long.toString(userModel.getId()))
                .putString(SESSION_USERNAME, userModel.getUsername())
                .putString(SESSION_USER_FIRSTNAME, userModel.getFirstname())
                .putString(SESSION_USER_LASTNAME, userModel.getLastname())
                .putString(SESSION_USER_EMAIL, userModel.getEmail())
                .putString(SESSION_USER_PIC_LINK, userModel.getUserPicLink())
                .commit();
    }

    public UserModel getSessionUser() {
        UserModel userModel = new UserModel();
        userModel.setEmail(getSessionUserEmail());
        userModel.setLastname(getSessionUserLastname());
        userModel.setFirstname(getSessionUserFirstname());
        userModel.setUserPicLink(getSessionUserPicUrl());
        userModel.setUsername(getSessionUsername());
        userModel.setId(getSessionUserId());
        return userModel;
    }

    public String getSessionId() {
        return sharedPreferences.getString(SESSION_ID, null);
    }

    public void setSessionUsername(String username) {
        editor.putString(SESSION_USERNAME, username).commit();
    }

    public String getSessionUsername() {
        return sharedPreferences.getString(SESSION_USERNAME, null);
    }

    public void setSessionUserFirstname(String firstname) {
        editor.putString(SESSION_USER_FIRSTNAME, firstname).commit();
    }

    public String getSessionUserFirstname() {
        return sharedPreferences.getString(SESSION_USER_FIRSTNAME, null);
    }

    public void setSessionUserLastname(String lastname) {
        editor.putString(SESSION_USER_LASTNAME, lastname).commit();
    }

    public String getSessionUserLastname() {
        return sharedPreferences.getString(SESSION_USER_LASTNAME, null);
    }

    public void setSessionUserEmail(String email) {
        editor.putString(SESSION_USER_EMAIL, email).commit();
    }

    public String getSessionUserEmail() {
        return sharedPreferences.getString(SESSION_USER_EMAIL, null);
    }

    public void setSessionUserPicUrl(String userPicUrl) {
        editor.putString(SESSION_USER_PIC_LINK, userPicUrl).commit();
    }

    public String getSessionUserPicUrl() {
        return sharedPreferences.getString(SESSION_USER_PIC_LINK, null);
    }

    public void setSessionUserId(Long id) {
        editor.putLong(SESSION_USER_ID, id).commit();
    }

    public Long getSessionUserId() {
        return sharedPreferences.getLong(SESSION_USER_ID, -1);
    }

    public void removeSession() {
        editor.putLong(SESSION_USER_ID, -1)
                .putString(SESSION_USERNAME, null)
                .putString(SESSION_USER_FIRSTNAME, null)
                .putString(SESSION_USER_LASTNAME, null)
                .putString(SESSION_USER_EMAIL, null)
                .putString(SESSION_USER_PIC_LINK, null)
                .putString(SESSION_ID, null)
                .commit();
    }

    public boolean isEmpty() {
        if (sharedPreferences == null || getSessionId() == null) {
            return true;
        }

        return false;
    }
}
