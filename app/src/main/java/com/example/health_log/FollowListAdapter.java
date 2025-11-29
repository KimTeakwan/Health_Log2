package com.example.health_log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.health_log.model.SimpleUser;

import java.util.List;

public class FollowListAdapter extends RecyclerView.Adapter<FollowListAdapter.UserViewHolder> {

    private Context context;
    private List<SimpleUser> userList;

    public FollowListAdapter(Context context, List<SimpleUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.follow_list_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        SimpleUser user = userList.get(position);
        holder.username.setText(user.getUsername());

        Glide.with(context)
             .load(user.getProfileImageUrl())
             .placeholder(R.drawable.ic_person)
             .error(R.drawable.ic_person)
             .circleCrop()
             .into(holder.profileImage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView username;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image_item);
            username = itemView.findViewById(R.id.username_item);
        }
    }
}
