package ru.netcracker.bikepacker;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.adapter.FindFriendAdapter;
import ru.netcracker.bikepacker.listholder.MyFriendsList;
import ru.netcracker.bikepacker.model.Friends;
import ru.netcracker.bikepacker.model.User;
import ru.netcracker.bikepacker.networkService.NetworkService;


public class FindFriendFragment extends Fragment {

    Context context;
    List<User> findFriendsList = new ArrayList<>();
    RecyclerView friendsRecyclerView;
    FindFriendAdapter friendAdapter;
    EditText findFriendSearchPlane;
    ImageButton searchFriendButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        context = getContext();
        View viewFindFriendFragment = inflater.inflate(R.layout.fragment_find_friend, container, false);

        findFriendSearchPlane = viewFindFriendFragment.findViewById(R.id.findFriendsPlaneText);
        searchFriendButton = viewFindFriendFragment.findViewById(R.id.searchFriendButton);

        displayMyFriend(viewFindFriendFragment);


        searchFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(findFriendSearchPlane.getText().toString());
                displayFindFriend(viewFindFriendFragment, findFriendSearchPlane.getText().toString());
            }
        });

        return viewFindFriendFragment;
    }

    private void setFindFragmentRecycler(List<User> findFriendsList, View view) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        // определяем слушателя нажатия элемента в списке
        FindFriendAdapter.OnStateClickListener friendClickListener = new FindFriendAdapter.OnStateClickListener() {

            @Override
            public void onStateClick(User user, int position) {

                Friends friends = new Friends("5", String.valueOf(user.getId()));

                NetworkService.getInstance()
                        .getJSONApi()
                        .postRequestFriend(friends)
                        .enqueue(new Callback<RequestBody>() {
                            @Override
                            public void onResponse(@NonNull Call<RequestBody> call, @NonNull Response<RequestBody> response) {
                                MyFriendsList.getInstance().addFriend(user);
                                Toast.makeText(getContext(), "Пользователь " + user.getFirstName() + " успешно добавлен", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<RequestBody> call, Throwable t) {
                                Toast.makeText(context, "Ошибка добавления", Toast.LENGTH_SHORT).show();
                                t.printStackTrace();
                            }
                        });
            }
        };
        friendsRecyclerView = view.findViewById(R.id.findFriendRecycler);
        friendsRecyclerView.setLayoutManager(layoutManager);

        friendAdapter = new FindFriendAdapter(getContext(), findFriendsList, friendClickListener);
        friendsRecyclerView.setAdapter(friendAdapter);
    }

    private void displayMyFriend(View view){
            NetworkService.getInstance()
                    .getJSONApi()
                    .getMyFriends("ComeBak")
                    .enqueue(new Callback<List<User>>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                            List<User> friends = response.body();
                            MyFriendsList.getInstance().updateMyFriends(friends);
                            findFriendsList.clear();
                            findFriendsList.addAll(friends);
                            setFindFragmentRecycler(findFriendsList, view);
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
                        System.out.println(friend);
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

}