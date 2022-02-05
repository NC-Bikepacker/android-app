package ru.netcracker.bikepacker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.listholder.MyFriendsList;
import ru.netcracker.bikepacker.model.Friends;
import ru.netcracker.bikepacker.model.User;
import ru.netcracker.bikepacker.networkService.NetworkService;

public class FindFriendAdapter extends RecyclerView.Adapter<FindFriendAdapter.FindFriendViewHolder> {

    Context context;
    List<User> findFriends;

    public interface OnStateClickListener{
        void onStateClick(User user, int position);
    }

    private final OnStateClickListener onClickListener;

    public FindFriendAdapter(Context context, List<User> findFriends, OnStateClickListener onClickListener) {
        this.context = context;
        this.findFriends = findFriends;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View findFriendsItem = LayoutInflater.from(context).inflate(R.layout.item_find_friend, parent, false);
        return new FindFriendViewHolder(findFriendsItem);
    }


    //устанавливаем значения полей для конкретного объекта в списке
    @Override
    public void onBindViewHolder(@NonNull FindFriendViewHolder holder, int position) {
           User user = findFriends.get(position);
           holder.firstName.setText(findFriends.get(position).getFirstName());
           holder.lastName.setText(findFriends.get(position).getLastName());
           holder.nickName.setText(findFriends.get(position).getNickName());

           Picasso.get()
                   .load(findFriends.get(holder.getAdapterPosition()).getUserPic_url())
                   .placeholder(R.drawable.ic_userpic)
                   .error(R.drawable.ic_userpic)
                   .into(holder.findFriend_pic);

           //обработчик кнопки
           holder.addFriendButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   onClickListener.onStateClick(user, position);
               }
           });
    }

    //устанавливаем размер RecycleView исходя из размера коллкекции findFriends
    @Override
    public int getItemCount() {
        return findFriends.size();
    }

    //вспомогательный класс view holder
    public static final class FindFriendViewHolder extends RecyclerView.ViewHolder{

        ImageView findFriend_pic;
        TextView firstName, lastName, nickName;
        ImageButton addFriendButton;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            findFriend_pic = itemView.findViewById(R.id.findFriendPic);
            firstName = itemView.findViewById(R.id.findFriend_firstName);
            lastName = itemView.findViewById(R.id.findFriend_lastName);
            nickName = itemView.findViewById(R.id.findFriend_nickName);
            addFriendButton = itemView.findViewById(R.id.findFriendAddButton);

        }
    }
}