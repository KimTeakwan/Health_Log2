package com.example.health_log;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.health_log.network.ApiService;
import com.example.health_log.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoDetailActivity extends AppCompatActivity {

    private static final String TAG = "VideoDetailActivity";

    private VideoView videoView;
    private TextView videoTitle;
    private TextView videoDescription;
    private Button likeButton;
    private RecyclerView commentsRecyclerView;
    private EditText commentEditText;
    private Button addCommentButton;

    private ApiService apiService;
    private int videoId;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private boolean isLiked = false;
    private int likesCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        videoView = findViewById(R.id.videoView);
        videoTitle = findViewById(R.id.videoTitle);
        videoDescription = findViewById(R.id.videoDescription);
        likeButton = findViewById(R.id.likeButton);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentEditText = findViewById(R.id.commentEditText);
        addCommentButton = findViewById(R.id.addCommentButton);

        apiService = RetrofitClient.getApiService();
        videoId = getIntent().getIntExtra("VIDEO_ID", -1);

        setupRecyclerView();
        getVideoDetails();

        likeButton.setOnClickListener(v -> likeVideo());
        addCommentButton.setOnClickListener(v -> postComment());
    }

    private void setupRecyclerView() {
        commentAdapter = new CommentAdapter(commentList, position -> {
            Comment comment = commentList.get(position);
            adoptComment(comment.getId());
        });
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentAdapter);
    }

    private void getVideoDetails() {
        if (videoId == -1) {
            Toast.makeText(this, "Video not found", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getVideo(videoId).enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Video video = response.body();
                    videoTitle.setText(video.getTitle());
                    videoDescription.setText(video.getDescription());
                    
                    // Store like status and count
                    isLiked = video.isLiked();
                    likesCount = video.getLikesCount();
                    updateLikeButtonUI();

                    String videoUrl = video.getVideoFile();
                    if (videoUrl != null && !videoUrl.isEmpty()) {
                        Uri uri = Uri.parse(videoUrl);
                        videoView.setVideoURI(uri);

                        MediaController mediaController = new MediaController(VideoDetailActivity.this);
                        mediaController.setAnchorView(videoView);
                        videoView.setMediaController(mediaController);
                        videoView.start();
                    } else {
                        Toast.makeText(VideoDetailActivity.this, "Video URL not available", Toast.LENGTH_SHORT).show();
                    }

                    commentList.clear();
                    commentList.addAll(video.getComments());
                    commentAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(VideoDetailActivity.this, "Failed to load video", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to load video: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                Toast.makeText(VideoDetailActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "An error occurred", t);
            }
        });
    }

    private void updateLikeButtonUI() {
        if (isLiked) {
            likeButton.setText("Liked (" + likesCount + ")");
        } else {
            likeButton.setText("Like (" + likesCount + ")");
        }
    }
    private void likeVideo() {
        // Optimistic UI Update
        isLiked = !isLiked;
        if (isLiked) {
            likesCount++;
        } else {
            likesCount--;
        }
        updateLikeButtonUI();

        apiService.likeVideo(videoId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // The backend call was successful. The UI is already updated.
                // We could re-fetch to ensure consistency, but for a like action, it's often not necessary.
                if (!response.isSuccessful()) {
                    // If the server failed, revert the change
                    isLiked = !isLiked;
                    if (isLiked) {
                        likesCount++;
                    } else {
                        likesCount--;
                    }
                    updateLikeButtonUI();
                    Toast.makeText(VideoDetailActivity.this, "Failed to like video", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // The network call failed, revert the change
                isLiked = !isLiked;
                if (isLiked) {
                    likesCount++;
                } else {
                    likesCount--;
                }
                updateLikeButtonUI();
                Toast.makeText(VideoDetailActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postComment() {
        String commentText = commentEditText.getText().toString().trim();
        if (commentText.isEmpty()) {
            return;
        }

        Comment comment = new Comment();
        comment.setText(commentText);

        apiService.postComment(videoId, comment).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Instead of refreshing everything, just add the new comment to the list
                    Comment newComment = response.body();
                    commentList.add(0, newComment);
                    commentAdapter.notifyItemInserted(0);
                    commentsRecyclerView.scrollToPosition(0);
                    commentEditText.setText("");
                } else {
                    Toast.makeText(VideoDetailActivity.this, "Failed to post comment: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(VideoDetailActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void adoptComment(int commentId) {
        apiService.adoptComment(commentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(VideoDetailActivity.this, "Comment adopted", Toast.LENGTH_SHORT).show();
                    getVideoDetails(); // Refresh to show adopted status
                } else {
                    Toast.makeText(VideoDetailActivity.this, "Failed to adopt comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(VideoDetailActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
