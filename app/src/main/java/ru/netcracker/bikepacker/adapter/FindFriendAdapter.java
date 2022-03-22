package ru.netcracker.bikepacker.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.listholder.MyFriendsList;
import ru.netcracker.bikepacker.model.UserModel;
import ru.netcracker.bikepacker.adapter.OnFriendClickListener;

public class FindFriendAdapter extends RecyclerView.Adapter<FindFriendAdapter.FindFriendViewHolder> {

    private Context context;
    private List<UserModel> findFriends;

    private final OnFriendClickListener onClickListener;

    public FindFriendAdapter(Context context, List<UserModel> findFriends, OnFriendClickListener onClickListener) {
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
           UserModel user = findFriends.get(position);
           holder.firstName.setText(findFriends.get(position).getFirstname());
           holder.lastName.setText(findFriends.get(position).getLastname());
           holder.nickName.setText(findFriends.get(position).getUsername());

           Picasso.get()
                   .load(findFriends.get(holder.getAdapterPosition()).getUserPicLink())
                   .placeholder(R.drawable.ic_userpic)
                   .error(R.drawable.ic_userpic)
                   .into(holder.findFriend_pic);

        //обработчик кнопки
        holder.addFriendButton.setOnClickListener(view -> onClickListener.addFriendClick(user, position));
        holder.deleteFriendButtton.setOnClickListener(view -> onClickListener.deleteFriendClick(user, position));

        //меняем кнопку на нужную, в зависимости от того, пользователь добавлен или удален
           if(MyFriendsList.getInstance().containsFriend(user.getId())){
               holder.addFriendButton.setVisibility(View.INVISIBLE);
               holder.deleteFriendButtton.setVisibility(View.VISIBLE);
           }
           else {
               holder.addFriendButton.setVisibility(View.VISIBLE);
               holder.deleteFriendButtton.setVisibility(View.INVISIBLE);
           }
    }

    //устанавливаем размер RecycleView исходя из размера коллкекции findFriends
    @Override
    public int getItemCount() {
        return findFriends.size();
    }

    //вспомогательный класс view holder
    public static final class FindFriendViewHolder extends RecyclerView.ViewHolder{

        private ImageView findFriend_pic;
        private TextView firstName, lastName, nickName;
        private ImageButton addFriendButton,deleteFriendButtton;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            findFriend_pic = itemView.findViewById(R.id.findFriendPic);
            firstName = itemView.findViewById(R.id.findFriend_firstName);
            lastName = itemView.findViewById(R.id.findFriend_lastName);
            nickName = itemView.findViewById(R.id.findFriend_nickName);
            addFriendButton = itemView.findViewById(R.id.findFriendAddButton);
            deleteFriendButtton = itemView.findViewById(R.id.deleteFriendButton);
        }
    }
}