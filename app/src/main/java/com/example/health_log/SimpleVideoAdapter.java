package com.example.health_log;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SimpleVideoAdapter extends RecyclerView.Adapter<SimpleVideoAdapter.ViewHolder> {

    private final Context context;
    private final List<PublicProfileActivity.SimpleVideo> videoList;

    public SimpleVideoAdapter(Context context, List<PublicProfileActivity.SimpleVideo> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_video_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PublicProfileActivity.SimpleVideo video = videoList.get(position);

        // Use Glide to load a thumbnail from the video URL.
        Glide.with(context)
                .load(video.getVideoFile())
                .placeholder(R.drawable.ic_launcher_background) // A generic placeholder
                .error(R.drawable.ic_launcher_background)       // An error placeholder
                .centerCrop()
                .into(holder.thumbnail);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoDetailActivity.class);
            intent.putExtra("VIDEO_ID", video.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.video_thumbnail);
        }
    }
}
