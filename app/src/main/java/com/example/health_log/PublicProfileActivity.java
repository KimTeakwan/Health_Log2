package com.example.health_log;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.health_log.network.ApiService;
import com.example.health_log.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicProfileActivity extends AppCompatActivity {

    public static final String EXTRA_USER_ID = "extra_user_id";

    private ImageView profileImageView;
    private TextView usernameTextView;
    private TextView instagramTextView;
    private TextView emailTextView;
    private TextView followerCountTextView;
    private TextView followingCountTextView;
    private Button followButton;
    private RecyclerView videosRecyclerView;
    private SimpleVideoAdapter videoAdapter;
    private int userId;
    private boolean isFollowing;
    private int followerCount;

    // --- Data Models for API Response ---
    // Note: It's better to move these to separate files in a 'models' package.
    public static class PublicProfileResponse {
        private int id;
        private String username;
        private String role;
        private ProfileData profile;
        private List<SimpleVideo> videos;
        private int follower_count;
        private int following_count;
        private boolean is_following;

        // Getters
        public int getId() { return id; }
        public String getUsername() { return username; }
        public ProfileData getProfile() { return profile; }
        public List<SimpleVideo> getVideos() { return videos; }
        public int getFollowerCount() { return follower_count; }
        public int getFollowingCount() { return following_count; }
        public boolean isFollowing() { return is_following; }
    }

    public static class ProfileData {
        private String public_email;
        private String instagram_id;
        private String profile_image_url;
        // Add other profile fields if needed (height, weight, specialty, etc.)

        // Getters
        public String getPublicEmail() { return public_email; }
        public String getInstagramId() { return instagram_id; }
        public String getProfileImageUrl() { return profile_image_url; }
    }

    public static class SimpleVideo {
        private int id;
        private String title;
        private String video_file; // URL of the video

        // Getters
        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getVideoFile() { return video_file; }
    }
    // --- End of Data Models ---


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);

        // Initialize Views
        profileImageView = findViewById(R.id.profile_image);
        usernameTextView = findViewById(R.id.username_text);
        instagramTextView = findViewById(R.id.instagram_text);
        emailTextView = findViewById(R.id.email_text);
        followerCountTextView = findViewById(R.id.follower_count_text);
        followingCountTextView = findViewById(R.id.following_count_text);
        followButton = findViewById(R.id.follow_button);
        videosRecyclerView = findViewById(R.id.videos_recycler_view);

        // Setup RecyclerView
        videosRecyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3 columns grid

        // Get user ID from intent
        userId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
        if (userId == -1) {
            Toast.makeText(this, "유저 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        followButton.setOnClickListener(v -> toggleFollowStatus());

        fetchUserProfile(userId);
    }

    private void fetchUserProfile(int userId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<PublicProfileResponse> call = apiService.getUserProfile(userId);

        call.enqueue(new Callback<PublicProfileResponse>() {
            @Override
            public void onResponse(Call<PublicProfileResponse> call, Response<PublicProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PublicProfileResponse profileResponse = response.body();
                    updateUi(profileResponse);
                } else {
                    Toast.makeText(PublicProfileActivity.this, "프로필 정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PublicProfileResponse> call, Throwable t) {
                Toast.makeText(PublicProfileActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUi(PublicProfileResponse data) {
        usernameTextView.setText(data.getUsername());

        // Store initial follow state and count
        isFollowing = data.isFollowing();
        followerCount = data.getFollowerCount();

        // Set follower/following counts
        followerCountTextView.setText(followerCount + "\nFollowers");
        followingCountTextView.setText(data.getFollowingCount() + "\nFollowing");

        // Set initial button state
        updateFollowButton();

        ProfileData profile = data.getProfile();
        if (profile != null) {
            // Load profile image
            if (profile.getProfileImageUrl() != null && !profile.getProfileImageUrl().isEmpty()) {
                Glide.with(this)
                     .load(profile.getProfileImageUrl())
                     .placeholder(R.drawable.ic_person)
                     .error(R.drawable.ic_person)
                     .into(profileImageView);
            }

            String instagramId = profile.getInstagramId();
            if (instagramId != null && !instagramId.isEmpty()) {
                instagramTextView.setText("인스타그램: " + instagramId);
            } else {
                instagramTextView.setVisibility(View.GONE);
            }

            String email = profile.getPublicEmail();
            if (email != null && !email.isEmpty()) {
                emailTextView.setText("이메일: " + email);
            } else {
                emailTextView.setVisibility(View.GONE);
            }
        } else {
            instagramTextView.setVisibility(View.GONE);
            emailTextView.setVisibility(View.GONE);
        }

        // Update RecyclerView with videos
        if (data.getVideos() != null && !data.getVideos().isEmpty()) {
            videoAdapter = new SimpleVideoAdapter(this, data.getVideos());
            videosRecyclerView.setAdapter(videoAdapter);
        } else {
            Toast.makeText(this, "업로드된 비디오가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleFollowStatus() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.followToggle(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Toggle the state and update UI
                    isFollowing = !isFollowing;
                    if (isFollowing) {
                        followerCount++;
                    } else {
                        followerCount--;
                    }
                    updateFollowButton();
                    followerCountTextView.setText(followerCount + "\nFollowers");
                } else {
                    Toast.makeText(PublicProfileActivity.this, "작업에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(PublicProfileActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFollowButton() {
        if (isFollowing) {
            followButton.setText("Following");
        } else {
            followButton.setText("Follow");
        }
    }
}
