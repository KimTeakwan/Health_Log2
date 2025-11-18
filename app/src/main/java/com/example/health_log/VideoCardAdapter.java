package com.example.health_log;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class VideoCardAdapter extends RecyclerView.Adapter<VideoCardAdapter.VideoCardViewHolder> {

    private List<Video> videoList;
    private Context context;

    public VideoCardAdapter(Context context, List<Video> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public VideoCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_card_item, parent, false);
        return new VideoCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoCardViewHolder holder, int position) {
        Video video = videoList.get(position);
        holder.title.setText(video.getTitle());
        if (video.getUploader() != null) {
            holder.uploader.setText(video.getUploader().getUsername());
        }
        holder.likesComments.setText(video.getLikesCount() + " likes  " + video.getComments().size() + " comments");
        holder.uploadDate.setText(video.getCreatedAt());

        holder.tags.removeAllViews();
        if (video.getTags() != null) {
            for (String tag : video.getTags()) {
                com.google.android.material.chip.Chip chip = new com.google.android.material.chip.Chip(context);
                chip.setText(tag);
                holder.tags.addView(chip);
            }
        }

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

    public static class VideoCardViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title;
        TextView uploader;
        TextView likesComments;
        TextView uploadDate;
        ChipGroup tags;

        public VideoCardViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.title);
            uploader = itemView.findViewById(R.id.uploader);
            likesComments = itemView.findViewById(R.id.likes_comments);
            uploadDate = itemView.findViewById(R.id.upload_date);
            tags = itemView.findViewById(R.id.tags);
        }
    }
}