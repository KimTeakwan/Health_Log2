package com.example.health_log;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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
                    likeButton.setText("Like (" + video.getLikesCount() + ")");

                    // TODO: Set video URI from video.getVideoFile()
                    // String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.sample_video;
                    // Uri uri = Uri.parse(videoPath);
                    // videoView.setVideoURI(uri);
                    // videoView.start();

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

    private void likeVideo() {
        apiService.likeVideo(videoId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    getVideoDetails(); // Refresh video details to update like count
                } else {
                    Toast.makeText(VideoDetailActivity.this, "Failed to like video", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
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
                if (response.isSuccessful()) {
                    commentEditText.setText("");
                    getVideoDetails(); // Refresh video details to show new comment
                } else {
                    Toast.makeText(VideoDetailActivity.this, "Failed to post comment", Toast.LENGTH_SHORT).show();
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
