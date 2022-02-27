package ru.netcracker.bikepacker;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.adapter.FindFriendAdapter;
import ru.netcracker.bikepacker.listholder.MyFriendsList;
import ru.netcracker.bikepacker.network.pojos.FriendDTO;
import ru.netcracker.bikepacker.network.NetworkService;
import ru.netcracker.bikepacker.network.pojos.UserDTO;


public class FindFriendFragment extends Fragment {

    private Context context;
    private List<UserDTO> findFriendsList = new ArrayList<>();
    private RecyclerView friendsRecyclerView;
    private FindFriendAdapter friendAdapter;
    private EditText findFriendSearchPlane;
    private ImageButton searchFriendButton;
    private View viewFindFriendFragment;
    //Юзер из под которого подключаемся
    private UserDTO iAmUser = new UserDTO(5L,"","","ComeBak","");

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


    private void setFindFragmentRecycler(List<UserDTO> findFriendsList, View view) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        friendsRecyclerView = view.findViewById(R.id.findFriendRecycler);
        friendsRecyclerView.setLayoutManager(layoutManager);

        friendAdapter = new FindFriendAdapter(getContext(), findFriendsList, clickListener());
        friendsRecyclerView.setAdapter(friendAdapter);
    }

    private void displayMyFriend(View view){
            NetworkService.getInstance(getContext())
                    .getJsonBackendAPI()
                    .getMyFriends(iAmUser.getUsername())
                    .enqueue(new Callback<List<UserDTO>>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                            List<UserDTO> friends = response.body();
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
                        public void onFailure(Call<List<UserDTO>> call, Throwable t) {
                            Toast.makeText(context, "Нет соединения с сервером!", Toast.LENGTH_SHORT).show();
                            Log.d(t.getMessage(), "Error occurred while getting request!");
                        }
                    });
    }

    private void displayFindFriend(View view, String nickName){
        NetworkService.getInstance(getContext())
                .getJsonBackendAPI()
                .getFriendWithNickName(nickName)
                .enqueue(new Callback<UserDTO>() {
                    @Override
                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                        UserDTO friend = response.body();
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
                    public void onFailure(Call<UserDTO> call, Throwable t) {
                        Toast.makeText(getContext(), "Ошибка поиска пользователя",Toast.LENGTH_SHORT).show();
                        Log.d(t.getMessage(), "Ошибка поиска пользователя");
                    }
                });

    }

    private FindFriendAdapter.OnFriendClickListener clickListener(){
        /* определяем слушателя нажатия элемента в списке */
        FindFriendAdapter.OnFriendClickListener friendClickListener = new FindFriendAdapter.OnFriendClickListener() {
            FriendDTO friends;
            @Override
            public void addFriendClick(UserDTO user, int position) {
                friends = new FriendDTO(String.valueOf(iAmUser.getId()), String.valueOf(user.getId()));
                NetworkService.getInstance(getContext())
                        .getJsonBackendAPI()
                        .postRequestFriend(friends)
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                                Toast.makeText(getContext(), "Заяка пользователю " + user.getFirstname() + " отправлена", Toast.LENGTH_SHORT).show();
                                displayFindFriend(viewFindFriendFragment, user.getUsername());
                            }
                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(context, "Ошибка добавления пользователя", Toast.LENGTH_SHORT).show();
                                displayFindFriend(viewFindFriendFragment, findFriendsList.get(position).getUsername());
                                Log.d(t.getMessage(), "Ошибка добавления пользователя");
                            }
                        });
            }

            @Override
            public void deleteFriendClick(UserDTO user, int position) {
                friends = new FriendDTO(String.valueOf(iAmUser.getId()), String.valueOf(user.getId()));
                NetworkService.getInstance(getContext())
                                .getJsonBackendAPI()
                                .deleteFriend(friends)
                                .enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        Toast.makeText(getContext(), "Пользователь " + user.getFirstname() + " удален", Toast.LENGTH_SHORT).show();
                                        MyFriendsList.getInstance().deleteFriend(user);
                                        displayMyFriend(viewFindFriendFragment);
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(getContext(), "Ошибка удаления", Toast.LENGTH_SHORT).show();
                                        displayMyFriend(viewFindFriendFragment);
                                        Log.d(t.getMessage(), "Ошибка удаления друга");
                                    }
                                });
            }

        };

        return friendClickListener;
    }
}