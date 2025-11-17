package com.example.health_log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat; // ✅ 추가됨: 날짜 포맷 변경 도구
import java.util.List;
import java.util.Locale;        // ✅ 추가됨: 지역 설정 도구

public class VideoCardAdapter extends RecyclerView.Adapter<VideoCardAdapter.VideoCardViewHolder> {

    private List<Video> videoList;

    public VideoCardAdapter(List<Video> videoList) {
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
        holder.uploader.setText(video.getUploader());
        holder.likesComments.setText(video.getLikes() + " likes  " + video.getComments() + " comments");

        // ✅ 수정된 부분: Date 객체를 String으로 변환
        if (video.getUploadDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            holder.uploadDate.setText(sdf.format(video.getUploadDate()));
        } else {
            holder.uploadDate.setText("");
        }

        holder.tags.removeAllViews();
        if (video.getTags() != null) {
            for (String tag : video.getTags()) {
                Chip chip = new Chip(holder.itemView.getContext());
                chip.setText(tag);
                holder.tags.addView(chip);
            }
        }
        // In a real app, you would use a library like Glide or Picasso to load the image
        // holder.thumbnail.setImageResource(R.drawable.placeholder);
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