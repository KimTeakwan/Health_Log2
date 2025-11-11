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

import java.util.List;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VideoCardAdapter extends RecyclerView.Adapter<VideoCardAdapter.VideoCardViewHolder> {

    private List<Video> videoList;
    private List<Video> videoListFull; // 원본 데이터 전체를 보관할 리스트

    public VideoCardAdapter(List<Video> videoList) {
        this.videoList = videoList;
        // 필터링을 위해 원본 리스트의 복사본을 만들어 둡니다.
        this.videoListFull = new ArrayList<>(videoList);
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

        // 날짜 형식을 "yyyy-MM-dd"로 지정하여 변환합니다.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        holder.uploadDate.setText(sdf.format(video.getUploadDate()));


        holder.tags.removeAllViews();
        for (String tag : video.getTags()) {
            Chip chip = new Chip(holder.itemView.getContext());
            chip.setText(tag);
            holder.tags.addView(chip);
        }
        // In a real app, you would use a library like Glide or Picasso to load the image
        // holder.thumbnail.setImageResource(R.drawable.placeholder);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public void filter(String text) {
        videoList.clear(); // 현재 리스트를 비웁니다.
        if (text.isEmpty()) {
            videoList.addAll(videoListFull); // 검색어가 없으면 전체 리스트를 보여줍니다.
        } else {
            text = text.toLowerCase();
            for (Video item : videoListFull) {
                // 비디오 제목에 검색어가 포함되어 있는지 확인 (원하는 조건으로 변경 가능)
                if (item.getTitle().toLowerCase().contains(text)) {
                    videoList.add(item);
                }
            }
        }
        notifyDataSetChanged(); // 어댑터에 데이터가 변경되었음을 알립니다.
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
