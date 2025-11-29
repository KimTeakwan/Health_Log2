package com.example.health_log;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import com.example.health_log.network.ApiService;
import com.example.health_log.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.core.content.ContextCompat;
import android.graphics.PorterDuff;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class VideoDetailActivity extends AppCompatActivity implements CommentAdapter.OnAdoptClickListener, CommentAdapter.OnReportClickListener {

    private static final String TAG = "VideoDetailActivity";

    private Toolbar toolbar;
    private VideoView videoView;
    private FrameLayout videoContainer;
    private TextView videoTitle;
    private TextView videoDescription;
    private ImageView likeIconDetail;
    private TextView likesCountDetail;
    private RecyclerView commentsRecyclerView;
    private EditText commentEditText;
    private ImageButton addCommentButton;

    private ApiService apiService;
    private int videoId;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private boolean isLiked = false;
    private int likesCount = 0;
    private boolean isDataChanged = false;

    private String videoUploaderId;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        videoView = findViewById(R.id.videoView);
        videoContainer = findViewById(R.id.video_container);
        videoTitle = findViewById(R.id.videoTitle);
        videoDescription = findViewById(R.id.videoDescription);
        likeIconDetail = findViewById(R.id.like_icon_detail);
        likesCountDetail = findViewById(R.id.likes_count_detail);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentEditText = findViewById(R.id.commentEditText);
        addCommentButton = findViewById(R.id.addCommentButton);

        apiService = RetrofitClient.getApiService();
        videoId = getIntent().getIntExtra("VIDEO_ID", -1);

        // Assuming SessionManager exists to get the current user's ID
        // currentUserId = SessionManager.getInstance(this).getUserId();


        setupRecyclerView();
        getVideoDetails();

        likeIconDetail.setOnClickListener(v -> likeVideo());
        addCommentButton.setOnClickListener(v -> postComment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishWithResult();
            return true;
        }
        if (item.getItemId() == R.id.action_report_video) {
            showReportDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finishWithResult();
    }

    private void finishWithResult() {
        Intent resultIntent = new Intent();
        if (isDataChanged) {
            setResult(RESULT_OK, resultIntent);
        } else {
            setResult(RESULT_CANCELED, resultIntent);
        }
        finish();
    }

    private void showReportDialog() {
        showReportContentDialog("video", videoId);
    }

    private void postVideoReport(String reason, String description) {
        if (videoId == -1) {
            Toast.makeText(this, "Video not found", Toast.LENGTH_SHORT).show();
            return;
        }

        ReportBody reportBody = new ReportBody("video", videoId, reason, description);
        apiService.reportContent(reportBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(VideoDetailActivity.this, "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(VideoDetailActivity.this, "신고 접수에 실패했습니다: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(VideoDetailActivity.this, "신고 접수에 실패했습니다: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(VideoDetailActivity.this, "오류가 발생했습니다: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        commentAdapter = new CommentAdapter(commentList, videoUploaderId, currentUserId, this, this);
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

                    if(video.getUploader() != null) {
                       videoUploaderId = String.valueOf(video.getUploader().getId());
                    }
                    // This is a placeholder. You need a real way to get the current user's ID.
                    // For example, from a SharedPreferences or a session manager class.
                    currentUserId = "1"; // Replace with actual current user ID logic


                    videoTitle.setText(video.getTitle());
                    videoDescription.setText(video.getDescription());
                    
                    // Store like status and count
                    isLiked = video.isLiked();
                    likesCount = video.getLikesCount();
                    updateLikeButtonUI();

                    String videoUrl = video.getVideoFile();
                    if (videoUrl != null && !videoUrl.isEmpty()) {
                        try {
                            Uri uri = Uri.parse(videoUrl);
                            videoView.setVideoURI(uri);

                            MediaController mediaController = new MediaController(VideoDetailActivity.this);
                            mediaController.setAnchorView(videoContainer);
                            videoView.setMediaController(mediaController);
                            videoView.start();
                        } catch (Exception e) {
                            Log.e(TAG, "Error setting video URI", e);
                            Toast.makeText(VideoDetailActivity.this, "Cannot play this video", Toast.LENGTH_SHORT).show();
                        }
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

    @Override
    public void onAdoptClick(int position) {
        Comment comment = commentList.get(position);
        adoptComment(comment.getId());
    }

    @Override
    public void onReportClick(int position) {
        Comment comment = commentList.get(position);
        showReportContentDialog("comment", comment.getId());
    }

    private void showReportContentDialog(final String contentType, final int objectId) {
        final String[] reasons = {"SPAM", "ABUSE", "INAPPROPRIATE", "OTHER"};
        final String[] displayReasons = {"스팸", "욕설/비방", "부적절한 콘텐츠", "기타"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = "comment".equals(contentType) ? "댓글 신고" : "동영상 신고";
        builder.setTitle(title);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_report, null);
        builder.setView(dialogView);

        final EditText descriptionInput = dialogView.findViewById(R.id.report_description_input);
        final TextView reasonSelector = dialogView.findViewById(R.id.report_reason_selector);

        reasonSelector.setOnClickListener(v -> {
            AlertDialog.Builder reasonBuilder = new AlertDialog.Builder(this);
            reasonBuilder.setTitle("신고 사유 선택");
            reasonBuilder.setItems(displayReasons, (dialog, which) -> {
                reasonSelector.setText(displayReasons[which]);
                reasonSelector.setTag(reasons[which]);
            });
            reasonBuilder.create().show();
        });

        builder.setPositiveButton("신고하기", (dialog, which) -> {
            String reason = (String) reasonSelector.getTag();
            if (reason == null) {
                Toast.makeText(this, "신고 사유를 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            String description = descriptionInput.getText().toString();

            if ("comment".equals(contentType)) {
                postCommentReport(objectId, reason, description);
            } else {
                postVideoReport(reason, description);
            }
        });
        builder.setNegativeButton("취소", null);

        builder.create().show();
    }

    private void postCommentReport(int commentId, String reason, String description) {
        ReportBody reportBody = new ReportBody("comment", commentId, reason, description);

        apiService.reportContent(reportBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(VideoDetailActivity.this, "댓글 신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(VideoDetailActivity.this, "신고 접수에 실패했습니다: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(VideoDetailActivity.this, "신고 접수에 실패했습니다: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(VideoDetailActivity.this, "오류가 발생했습니다: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateLikeButtonUI() {
        likesCountDetail.setText(String.valueOf(likesCount));
        if (isLiked) {
            // Set tint to an active color to indicate "liked" state
            int activeColor = ContextCompat.getColor(this, android.R.color.holo_blue_light);
            likeIconDetail.setColorFilter(activeColor, PorterDuff.Mode.SRC_IN);
        } else {
            // Set tint to a neutral color to indicate "not liked" state
            int inactiveColor = ContextCompat.getColor(this, android.R.color.darker_gray);
            likeIconDetail.setColorFilter(inactiveColor, PorterDuff.Mode.SRC_IN);
        }
    }
        private void likeVideo() {
            final boolean originalIsLiked = isLiked;
            final int originalLikesCount = likesCount;
    
            // Optimistic UI Update
            isLiked = !isLiked;
            likesCount = isLiked ? likesCount + 1 : likesCount - 1;
            updateLikeButtonUI();
    
            retrofit2.Callback<Void> callback = new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        isDataChanged = true;
                    } else {
                        // If the server failed, revert the change
                        isLiked = originalIsLiked;
                        likesCount = originalLikesCount;
                        updateLikeButtonUI();
                        Toast.makeText(VideoDetailActivity.this, "Failed to update like status", Toast.LENGTH_SHORT).show();
                    }
                }
    
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // The network call failed, revert the change
                    isLiked = originalIsLiked;
                    likesCount = originalLikesCount;
                    updateLikeButtonUI();
                    Toast.makeText(VideoDetailActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
    
            if (originalIsLiked) { // Was liked, so now unliking
                apiService.unlikeVideo(videoId).enqueue(callback);
            } else { // Was not liked, so now liking
                apiService.likeVideo(videoId).enqueue(callback);
            }
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
                        isDataChanged = true;
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
                        isDataChanged = true;
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
