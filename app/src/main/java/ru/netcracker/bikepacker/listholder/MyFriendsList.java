package ru.netcracker.bikepacker.listholder;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ru.netcracker.bikepacker.model.UserModel;


public class MyFriendsList {

    private static MyFriendsList myFriendList;
    private static List<Long> idMyfriends;

    public MyFriendsList() {
        idMyfriends = new ArrayList<>();
    }

    public static MyFriendsList getInstance() {
        if (myFriendList == null) {
            myFriendList = new MyFriendsList();
        }
        return myFriendList;
    }

    public void addFriend(UserModel user){
        idMyfriends.add(user.getId());
    }

    public void deleteFriend(UserModel user){
        idMyfriends.remove(user.getId());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateMyFriends(List<UserModel> users){
        idMyfriends.clear();
        idMyfriends.addAll(users.stream()
                                .map(UserModel::getId)
                                .collect(Collectors.toList())
                            );
    }

    public boolean containsFriend(Long userId){

        return idMyfriends.contains(userId);
    }

}
