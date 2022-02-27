package ru.netcracker.bikepacker.listholder;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ru.netcracker.bikepacker.network.pojos.UserDTO;

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

    public void addFriend(UserDTO user){
        idMyfriends.add(user.getId());
    }

    public void deleteFriend(UserDTO user){
        idMyfriends.remove(user.getId());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateMyFriends(List<UserDTO> users){
        idMyfriends.clear();
        idMyfriends.addAll(users.stream()
                                .map(UserDTO::getId)
                                .collect(Collectors.toList())
                            );
    }

    public boolean containsFriend(Long userId){

        return idMyfriends.contains(userId);
    }

}
