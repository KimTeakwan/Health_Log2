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

    public interface OnAdoptClickListener {
        void onAdoptClick(int position);
    }

    public CommentAdapter(List<Comment> comments, OnAdoptClickListener listener) {
        this.comments = comments;
        this.adoptClickListener = listener;
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
            holder.commentUser.setText(comment.getUser().getUsername());
        }
        holder.commentText.setText(comment.getText());

        // Show adopt button only for trainer's comments
        if (comment.getUser() != null && "trainer".equals(comment.getUser().getRole())) {
            holder.adoptButton.setVisibility(View.VISIBLE);
            holder.adoptButton.setOnClickListener(v -> {
                if (adoptClickListener != null) {
                    adoptClickListener.onAdoptClick(position);
                }
            });
        } else {
            holder.adoptButton.setVisibility(View.GONE);
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

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentUser = itemView.findViewById(R.id.commentUser);
            commentText = itemView.findViewById(R.id.commentText);
            adoptButton = itemView.findViewById(R.id.adoptButton);
        }
    }
}
