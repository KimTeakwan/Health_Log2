package com.example.health_log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> comments;
    private OnAdoptClickListener adoptClickListener;
    private OnReportClickListener reportClickListener;
    private String videoUploaderId;
    private String currentUserId;


    public interface OnAdoptClickListener {
        void onAdoptClick(int position);
    }

    public interface OnReportClickListener {
        void onReportClick(int position);
    }

    public CommentAdapter(List<Comment> comments, String videoUploaderId, String currentUserId, OnAdoptClickListener adoptListener, OnReportClickListener reportListener) {
        this.comments = comments;
        this.videoUploaderId = videoUploaderId;
        this.currentUserId = currentUserId;
        this.adoptClickListener = adoptListener;
        this.reportClickListener = reportListener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        if (comment.getUser() != null) {
            holder.commentUser.setText(comment.getUser().getFirstName());
        }
        holder.commentText.setText(comment.getText());

        boolean isVideoUploader = currentUserId != null && currentUserId.equals(videoUploaderId);

        if (comment.isAdopted()) {
            holder.adoptedBadge.setVisibility(View.VISIBLE);
            holder.adoptButton.setVisibility(View.GONE);
            holder.reportButton.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_light, null));
        } else {
            holder.adoptedBadge.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent, null));

            if (isVideoUploader) {
                // Show adopt button for trainer's comments if not adopted yet
                if (comment.getUser() != null && "trainer".equals(comment.getUser().getRole())) {
                    holder.adoptButton.setVisibility(View.VISIBLE);
                    holder.adoptButton.setOnClickListener(v -> {
                        if (adoptClickListener != null) {
                            adoptClickListener.onAdoptClick(holder.getAdapterPosition());
                        }
                    });
                } else {
                    holder.adoptButton.setVisibility(View.GONE);
                }

                // Show report button if the current user is the video uploader and the comment is not their own
                if (comment.getUser() != null && !currentUserId.equals(String.valueOf(comment.getUser().getId()))) {
                    holder.reportButton.setVisibility(View.VISIBLE);
                    holder.reportButton.setOnClickListener(v -> {
                        if (reportClickListener != null) {
                            reportClickListener.onReportClick(holder.getAdapterPosition());
                        }
                    });
                } else {
                    holder.reportButton.setVisibility(View.GONE);
                }
            } else {
                holder.adoptButton.setVisibility(View.GONE);
                holder.reportButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentUser;
        TextView commentText;
        Button adoptButton;
        Button reportButton; // Changed from reportButton
        TextView adoptedBadge;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentUser = itemView.findViewById(R.id.commentUser);
            commentText = itemView.findViewById(R.id.commentText);
            adoptButton = itemView.findViewById(R.id.adoptButton);
            reportButton = itemView.findViewById(R.id.btn_report_comment); // Changed from R.id.reportButton
            adoptedBadge = itemView.findViewById(R.id.adoptedBadge);
        }
    }
}
