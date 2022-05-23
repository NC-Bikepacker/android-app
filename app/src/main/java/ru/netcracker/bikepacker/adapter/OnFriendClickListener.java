package ru.netcracker.bikepacker.adapter;

import ru.netcracker.bikepacker.manager.UserAccountManager;
import ru.netcracker.bikepacker.model.UserModel;

public interface OnFriendClickListener {
    void addFriendClick(UserModel user, int position);
    void deleteFriendClick(UserModel user, int position);
    void showUserTracks(UserModel user, UserAccountManager userAccountManager);
}
