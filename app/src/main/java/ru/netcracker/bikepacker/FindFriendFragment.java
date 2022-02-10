package ru.netcracker.bikepacker;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.adapter.FindFriendAdapter;
import ru.netcracker.bikepacker.listholder.MyFriendsList;
import ru.netcracker.bikepacker.model.Friend;
import ru.netcracker.bikepacker.model.User;
import ru.netcracker.bikepacker.networkService.NetworkService;


public class FindFriendFragment extends Fragment {

    Context context;
    List<User> findFriendsList = new ArrayList<>();
    RecyclerView friendsRecyclerView;
    FindFriendAdapter friendAdapter;
    EditText findFriendSearchPlane;
    ImageButton searchFriendButton;
    View viewFindFriendFragment;
    //Юзер из под которого подключаемся
    User iAmUser = new User(5L,"","","ComeBak","");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //основной метод для работы с Fragment
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        //получаем context
        context = getContext();

        //устанавливаем fragment для текущего view
        viewFindFriendFragment = inflater.inflate(R.layout.fragment_find_friend, container, false);

        //инициализируем строку поиска в fragment find для получения информации из неё
        findFriendSearchPlane = viewFindFriendFragment.findViewById(R.id.findFriendsPlaneText);

        //инициализируем кнопку searchFriendButton
        searchFriendButton = viewFindFriendFragment.findViewById(R.id.searchFriendButton);

        //метод для получения "моих друзей" и вывода их в RecycleView
        displayMyFriend(viewFindFriendFragment);

        //обработчик нажатия кнопки searchFriendButton для начала поиска
        searchFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                displayFindFriend(viewFindFriendFragment, findFriendSearchPlane.getText().toString());
            }
        });

        return viewFindFriendFragment;
    }


    private void setFindFragmentRecycler(List<User> findFriendsList, View view) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        friendsRecyclerView = view.findViewById(R.id.findFriendRecycler);
        friendsRecyclerView.setLayoutManager(layoutManager);

        friendAdapter = new FindFriendAdapter(getContext(), findFriendsList, clickListener());
        friendsRecyclerView.setAdapter(friendAdapter);
    }

    private void displayMyFriend(View view){
            NetworkService.getInstance()
                    .getJSONApi()
                    .getMyFriends(iAmUser.getNickName())
                    .enqueue(new Callback<List<User>>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                            List<User> friends = response.body();
                            if(friends.isEmpty()){
                                Toast.makeText(context, "у вас еще нет друзей", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                MyFriendsList.getInstance().updateMyFriends(friends);
                                findFriendsList.clear();
                                findFriendsList.addAll(friends);
                                setFindFragmentRecycler(findFriendsList, view);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<User>> call, Throwable t) {
                            Toast.makeText(context, "Error occurred while getting request!", Toast.LENGTH_SHORT).show();
                            t.printStackTrace();
                        }
                    });
    }

    private void displayFindFriend(View view, String nickName){
        NetworkService.getInstance()
                .getJSONApi()
                .getFriendWithNickName(nickName)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User friend = response.body();
                        findFriendsList.clear();
                        if(friend == null){
                            Toast.makeText(getContext(), "Пользователи не найдены",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            findFriendsList.add(friend);
                            setFindFragmentRecycler(findFriendsList, view);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(getContext(), "Ошибка поиска пользователя",Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private FindFriendAdapter.OnFriendClickListener clickListener(){
        /* определяем слушателя нажатия элемента в списке */
        FindFriendAdapter.OnFriendClickListener friendClickListener = new FindFriendAdapter.OnFriendClickListener() {
            Friend friends;
            @Override
            public void addFriendClick(User user, int position) {
                friends = new Friend(String.valueOf(iAmUser.getId()), String.valueOf(user.getId()));
                NetworkService.getInstance()
                        .getJSONApi()
                        .postRequestFriend(friends)
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                                Toast.makeText(getContext(), "Заяка пользователю " + user.getFirstName() + " отправлена", Toast.LENGTH_SHORT).show();
                                displayFindFriend(viewFindFriendFragment, user.getNickName());
                                System.out.println(findFriendsList.get(position).getNickName());
                            }
                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(context, "Ошибка добавления пользователя", Toast.LENGTH_SHORT).show();
                                System.out.println("Ошибка: " + t.getMessage());
                                displayFindFriend(viewFindFriendFragment, findFriendsList.get(position).getNickName());
                            }
                        });
            }

            @Override
            public void deleteFriendClick(User user, int position) {
                friends = new Friend(String.valueOf(iAmUser.getId()), String.valueOf(user.getId()));
                NetworkService.getInstance()
                                .getJSONApi()
                                .deleteFriend(friends)
                                .enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        Toast.makeText(getContext(), "Пользователь " + user.getFirstName() + " удален", Toast.LENGTH_SHORT).show();
                                        MyFriendsList.getInstance().deleteFriend(user);
                                        System.out.println(response.message());
                                        displayMyFriend(viewFindFriendFragment);
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(getContext(), "Ошибка удаления", Toast.LENGTH_SHORT).show();
                                        displayMyFriend(viewFindFriendFragment);
                                        System.out.println("Ошибка: " + t.getMessage());
                                    }
                                });
            }

        };

        return friendClickListener;
    }
}