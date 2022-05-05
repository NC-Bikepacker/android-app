package ru.netcracker.bikepacker.view;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.dynamic.SupportFragmentWrapper;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.adapter.FindFriendAdapter;
import ru.netcracker.bikepacker.adapter.OnFriendClickListener;
import ru.netcracker.bikepacker.adapter.TracksRecyclerAdapter;
import ru.netcracker.bikepacker.databinding.ActivityMainNavigationBinding;
import ru.netcracker.bikepacker.listholder.MyFriendsList;
import ru.netcracker.bikepacker.manager.RetrofitManager;
import ru.netcracker.bikepacker.manager.SessionManager;
import ru.netcracker.bikepacker.manager.UserAccountManager;
import ru.netcracker.bikepacker.model.FriendModel;
import ru.netcracker.bikepacker.model.TrackModel;
import ru.netcracker.bikepacker.model.UserModel;


public class FindFriendFragment extends Fragment {

    private Context context;
    private List<UserModel> findFriendsList = new ArrayList<>();
    private RecyclerView friendsRecyclerView;
    private FindFriendAdapter friendAdapter;
    private EditText findFriendSearchPlane;
    private ImageButton searchFriendButton;
    private View viewFindFriendFragment;
    private SessionManager sessionManager;
    //Юзер из под которого подключаемся
    private UserModel iAmUser;
    private String cookie;
    private OnFriendClickListener clickListener;

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

        //инициализаруем session manager
        sessionManager = SessionManager.getInstance(context);

        cookie = UserAccountManager.getInstance(context).getCookie();

        //получаем sessionUser
        iAmUser = sessionManager.getSessionUser();

        //attach clickListener
        this.clickListener = new OnFriendClickListener() {
            FriendModel friends;
            @Override
            public void addFriendClick(UserModel user, int position) {
                friends = new FriendModel(String.valueOf(iAmUser.getId()), String.valueOf(user.getId()));
                RetrofitManager.getInstance(getContext())
                        .getJSONApi()
                        .postRequestFriend(cookie, friends)
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
            public void deleteFriendClick(UserModel user, int position) {
                friends = new FriendModel(String.valueOf(iAmUser.getId()), String.valueOf(user.getId()));
                RetrofitManager.getInstance(getContext())
                        .getJSONApi()
                        .deleteFriend(cookie, friends)
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

            @Override
            public void showUserTracks(UserModel user, UserAccountManager userAccountManager) {
                List<TrackModel> tracks = new ArrayList<>();
                RetrofitManager.getInstance(getContext())
                        .getJSONApi()
                        .getTracksByUser(userAccountManager.getCookie(), user.getId())
                        .enqueue(new Callback<List<TrackModel>>() {
                            @Override
                            public void onResponse(Call<List<TrackModel>> call, Response<List<TrackModel>> response) {
                                if(response.isSuccessful()){
                                    tracks.clear();
                                    tracks.addAll(response.body());

                                    ShowTracksFragment showTracksFragment = new ShowTracksFragment(tracks);

                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .add(R.id.fragment_container, showTracksFragment, "TAG_SHOW_TRACKS")
                                            .hide(MainNavigationActivity.Companion.getActiveFragment())
                                            .show(showTracksFragment)
                                            .commit();
                                    MainNavigationActivity.Companion.setActiveFragment(showTracksFragment);
                                }
                                else {
                                    Toast.makeText(getContext(), "Проверьте соединение интернет", Toast.LENGTH_SHORT).show();
                                    Log.e("Error show friend tracks", "Error response successful response. Error message: "+ response.message() + ". Error code: " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<List<TrackModel>> call, Throwable t) {
                                Toast.makeText(getContext(), "Ошибка вывода треков пользователя " + "@" + user.getUsername() + ". Проверьте соединение интернет", Toast.LENGTH_SHORT).show();
                                Log.e("Error show friend tracks", "Error user track response. Error message: "+ t.getMessage(), t);
                            }
                        });
            }
        };

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


    private void setFindFragmentRecycler(List<UserModel> findFriendsList, View view) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        friendsRecyclerView = view.findViewById(R.id.findFriendRecycler);
        friendsRecyclerView.setLayoutManager(layoutManager);

        friendAdapter = new FindFriendAdapter(getContext(), findFriendsList, clickListener);
        friendsRecyclerView.setAdapter(friendAdapter);
    }

    public void displayMyFriend(View view) {
        RetrofitManager.getInstance(getContext())
                .getJSONApi()
                .getMyFriends(cookie, iAmUser.getUsername())
                .enqueue(new Callback<List<UserModel>>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                        List<UserModel> friends = response.body();
                        if (friends != null && !friends.isEmpty()) {
                            MyFriendsList.getInstance().updateMyFriends(friends);
                            findFriendsList.clear();
                            findFriendsList.addAll(friends);
                            setFindFragmentRecycler(findFriendsList, view);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UserModel>> call, Throwable t) {
                        Toast.makeText(context, "Нет соединения с сервером!", Toast.LENGTH_SHORT).show();
                        Log.d(t.getMessage(), "Error occurred while getting request!");
                    }
                });
    }

    private void displayFindFriend(View view, String nickName) {
        RetrofitManager.getInstance(getContext())
                .getJSONApi()
                .getFriendWithNickName(cookie, nickName)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        UserModel friend = response.body();
                        findFriendsList.clear();
                        if (friend == null) {
                            Toast.makeText(getContext(), "Пользователи не найдены", Toast.LENGTH_SHORT).show();
                        } else {
                            findFriendsList.add(friend);
                            setFindFragmentRecycler(findFriendsList, view);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        Toast.makeText(getContext(), "Ошибка поиска пользователя", Toast.LENGTH_SHORT).show();
                        Log.d(t.getMessage(), "Ошибка поиска пользователя");
                    }
                });

    }

    public void disp() {
        displayMyFriend(viewFindFriendFragment);
    }
}