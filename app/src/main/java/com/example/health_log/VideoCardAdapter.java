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
import com.example.health_log.network.ApiService;
import com.example.health_log.network.RetrofitClient;
import android.app.Activity;


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
            holder.uploader.setText(video.getUploader().getFirstName());
            holder.uploader.setOnClickListener(v -> {
                Intent intent = new Intent(context, PublicProfileActivity.class);
                intent.putExtra(PublicProfileActivity.EXTRA_USER_ID, video.getUploader().getId());
                context.startActivity(intent);
            });
        }
        // holder.likesComments.setText(video.getLikesCount() + " likes  " + video.getComments().size() + " comments"); // Removed as part of UI redesign
        holder.likesCount.setText(String.valueOf(video.getLikesCount()));
        holder.commentCount.setText(String.valueOf(video.getComments().size()));
        holder.viewCount.setText(String.valueOf(video.getViewCount()));
        String rawDate = video.getCreatedAt();
        if (rawDate != null && rawDate.contains("T")) {
            String formattedDate = rawDate.split("T")[0];
            holder.uploadDate.setText(formattedDate);
        } else {
            holder.uploadDate.setText(rawDate); // Fallback to raw date if format is unexpected
        }

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
            if (context instanceof MainActivity) {
                ((MainActivity) context).startActivityForResult(intent, MainActivity.VIDEO_DETAIL_REQUEST_CODE);
            } else if (context instanceof UserProfileActivity) {
                ((UserProfileActivity) context).startActivityForResult(intent, UserProfileActivity.VIDEO_DETAIL_REQUEST_CODE);
            }
            else {
                context.startActivity(intent);
            }
        });

        updateLikeButton(holder.likeIcon, video.isLiked());

        holder.likeIcon.setOnClickListener(v -> {
            boolean originalLikedState = video.isLiked();
            int originalLikesCount = video.getLikesCount();

            // Optimistic UI update
            video.setLiked(!originalLikedState);
            video.setLikesCount(originalLikedState ? originalLikesCount - 1 : originalLikesCount + 1);
            notifyItemChanged(holder.getAdapterPosition());

            ApiService apiService = RetrofitClient.getApiService();

            retrofit2.Callback<Void> callback = new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                    // Success, do nothing as UI is already updated
                }

                @Override
                public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                    // On failure, revert UI changes
                    video.setLiked(originalLikedState);
                    video.setLikesCount(originalLikesCount);
                    notifyItemChanged(holder.getAdapterPosition());
                    android.widget.Toast.makeText(context, "좋아요 처리 중 오류가 발생했습니다.", android.widget.Toast.LENGTH_SHORT).show();
                }
            };

            if (originalLikedState) { // Was liked, so now unliking
                apiService.unlikeVideo(video.getId()).enqueue(callback);
            } else { // Was not liked, so now liking
                apiService.likeVideo(video.getId()).enqueue(callback);
            }
        });
    }

    private void updateLikeButton(ImageView likeIcon, boolean isLiked) {
        if (isLiked) {
            likeIcon.setColorFilter(context.getResources().getColor(R.color.colorAccent, context.getTheme()));
        } else {
            likeIcon.clearColorFilter();
        }
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoCardViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title;
        TextView uploader;
        // TextView likesComments; // Removed as part of UI redesign
        ImageView likeIcon;
        TextView likesCount;
        TextView commentCount;
        TextView viewCount;
        TextView uploadDate;
        ChipGroup tags;

        public VideoCardViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.title);
            uploader = itemView.findViewById(R.id.uploader);
            // likesComments = itemView.findViewById(R.id.likes_comments); // Removed as part of UI redesign
            likeIcon = itemView.findViewById(R.id.like_icon);
            likesCount = itemView.findViewById(R.id.likes_count);
            commentCount = itemView.findViewById(R.id.comment_count);
            viewCount = itemView.findViewById(R.id.view_count);
            uploadDate = itemView.findViewById(R.id.upload_date);
            tags = itemView.findViewById(R.id.tags);
        }
    }
}