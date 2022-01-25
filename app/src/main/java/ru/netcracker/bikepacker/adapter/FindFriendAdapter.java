package ru.netcracker.bikepacker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.model.FindFriendEntity;

public class FindFriendAdapter extends RecyclerView.Adapter<FindFriendAdapter.FindFriendViewHolder> {

    Context context;
    List<FindFriendEntity> findFriends;

    public FindFriendAdapter(Context context, List<FindFriendEntity> findFriends) {
        this.context = context;
        this.findFriends = findFriends;
    }

    @NonNull
    @Override
    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View findFriendsItem = LayoutInflater.from(context).inflate(R.layout.item_find_friend, parent, false);
        return new FindFriendViewHolder(findFriendsItem);
    }

    @Override
    public void onBindViewHolder(@NonNull FindFriendViewHolder holder, int position) {
        holder.firstName.setText(findFriends.get(position).getFirstName());
        holder.lastName.setText(findFriends.get(position).getLastName());
        holder.nickName.setText(findFriends.get(position).getNickName());

        Picasso.get()
                .load(findFriends.get(position).getUserPic_url())
                .placeholder(R.drawable.ic_userpic)
                .error(R.drawable.ic_userpic)
                .into(holder.findFriend_pic);
    }

    @Override
    public int getItemCount() {
        return findFriends.size();
    }

    public static final class FindFriendViewHolder extends RecyclerView.ViewHolder{

        ImageView findFriend_pic;
        TextView firstName, lastName, nickName;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            findFriend_pic = itemView.findViewById(R.id.findFriendPic);
            firstName = itemView.findViewById(R.id.findFriend_firstName);
            lastName = itemView.findViewById(R.id.findFriend_lastName);
            nickName = itemView.findViewById(R.id.findFriend_nickName);
        }
    }
}
