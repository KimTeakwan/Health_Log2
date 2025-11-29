package com.example.health_log;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull; // Add this import
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.health_log.network.ApiService;
import com.example.health_log.network.RetrofitClient;
import com.google.android.gms.tasks.OnFailureListener; // Add this import
import com.google.android.gms.tasks.OnSuccessListener; // Add this import
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage; // Add this import
import com.google.firebase.storage.UploadTask; // Add this import
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID; // Add this import

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {

    private static final int EDIT_PROFILE_REQUEST_CODE = 200;
    public static final int VIDEO_DETAIL_REQUEST_CODE = 1001;
    private static final String TAG = "UserProfileActivity";

    private ImageView profileImageView;
    private TextView profileNameTextView;
    private TextView profileEmailTextView; // Added for email display
    private TextView adoptedCommentsCountTextView;
    private TextView followerCountTextView;
    private TextView followingCountTextView;
    private ProgressBar progressBar;
    private View contentLayout;

    private ApiService apiService;
    private FirebaseStorage storage; // FirebaseStorage instance
    private String currentUsername;
    private String currentProfileImageUrl;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        apiService = RetrofitClient.getApiService();
        storage = FirebaseStorage.getInstance();

        profileImageView = findViewById(R.id.profile_image);
        profileNameTextView = findViewById(R.id.profile_name);
        profileEmailTextView = findViewById(R.id.profile_email);
        adoptedCommentsCountTextView = findViewById(R.id.adopted_comments_count);
        followerCountTextView = findViewById(R.id.follower_count_text);
        followingCountTextView = findViewById(R.id.following_count_text);
        progressBar = findViewById(R.id.progress_bar);
        contentLayout = findViewById(R.id.content_layout);

        setupButtons();
        loadProfileData();
    }

    private void setupButtons() {
        Button logoutButton = findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            // Clear any local user data if necessary
            Toast.makeText(UserProfileActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        Button editProfileButton = findViewById(R.id.btn_edit_profile);
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("nickname", currentUsername);
            intent.putExtra("imageUri", currentProfileImageUrl);
            startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
        });

        followerCountTextView.setOnClickListener(v -> {
            if (currentUserId == null) return;
            Intent intent = new Intent(UserProfileActivity.this, FollowListActivity.class);
            intent.putExtra(FollowListActivity.EXTRA_USER_ID, currentUserId);
            intent.putExtra(FollowListActivity.EXTRA_LIST_TYPE, "followers");
            startActivity(intent);
        });

        followingCountTextView.setOnClickListener(v -> {
            if (currentUserId == null) return;
            Intent intent = new Intent(UserProfileActivity.this, FollowListActivity.class);
            intent.putExtra(FollowListActivity.EXTRA_USER_ID, currentUserId);
            intent.putExtra(FollowListActivity.EXTRA_LIST_TYPE, "following");
            startActivity(intent);
        });
    }

    private void loadProfileData() {
        progressBar.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);
        apiService.getMyProfile().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                progressBar.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject fullProfileResponse = response.body().getAsJsonObject();

                    if (fullProfileResponse.has("user_id") && !fullProfileResponse.get("user_id").isJsonNull()) {
                        currentUserId = fullProfileResponse.get("user_id").getAsString();
                    }

                    String userRole = null;
                    if (fullProfileResponse.has("role") && !fullProfileResponse.get("role").isJsonNull()) {
                        userRole = fullProfileResponse.get("role").getAsString();
                    }

                    JsonObject profile = null;
                    if (fullProfileResponse.has("profile") && !fullProfileResponse.get("profile").isJsonNull()) {
                        profile = fullProfileResponse.get("profile").getAsJsonObject();
                    }

                    // Parse first_name from the API response, handling both flat and nested structures
                    if (fullProfileResponse.has("first_name") && !fullProfileResponse.get("first_name").isJsonNull()) {
                        // Handles 'user' role structure
                        currentUsername = fullProfileResponse.get("first_name").getAsString();
                    } else if (fullProfileResponse.has("user") && fullProfileResponse.get("user").isJsonObject()) {
                        // Handles 'trainer' role structure
                        JsonObject userObject = fullProfileResponse.get("user").getAsJsonObject();
                        if (userObject.has("first_name") && !userObject.get("first_name").isJsonNull()) {
                            currentUsername = userObject.get("first_name").getAsString();
                        }
                    } else {
                        // Fallback if first_name is not available in either structure
                        currentUsername = "My Profile";
                    }

                    profileNameTextView.setText(currentUsername);

                    // Display public email
                    if (profile != null && profile.has("public_email") && !profile.get("public_email").isJsonNull()) {
                        profileEmailTextView.setText(profile.get("public_email").getAsString());
                        profileEmailTextView.setVisibility(View.VISIBLE);
                    } else {
                        profileEmailTextView.setVisibility(View.GONE);
                    }

                    // Load profile image
                    if (fullProfileResponse.has("profile_image_url") && !fullProfileResponse.get("profile_image_url").isJsonNull()) {
                        currentProfileImageUrl = fullProfileResponse.get("profile_image_url").getAsString();
                        Glide.with(UserProfileActivity.this)
                                .load(currentProfileImageUrl)
                                .placeholder(R.drawable.ic_person)
                                .error(R.drawable.ic_person)
                                .into(profileImageView);
                    } else {
                        profileImageView.setImageResource(R.drawable.ic_person); // Default image
                        currentProfileImageUrl = null; // Clear if no URL from backend
                    }

                    if ("trainer".equals(userRole) && fullProfileResponse.has("adopted_comment_count") && !fullProfileResponse.get("adopted_comment_count").isJsonNull()) {
                        int adoptedCommentCount = fullProfileResponse.get("adopted_comment_count").getAsInt();
                        adoptedCommentsCountTextView.setText("채택된 댓글: " + adoptedCommentCount + "개");
                        adoptedCommentsCountTextView.setVisibility(View.VISIBLE);
                    } else {
                        adoptedCommentsCountTextView.setVisibility(View.GONE);
                    }

                    // Display follower and following counts
                    if (fullProfileResponse.has("follower_count") && !fullProfileResponse.get("follower_count").isJsonNull()) {
                        int followerCount = fullProfileResponse.get("follower_count").getAsInt();
                        followerCountTextView.setText("팔로워: " + followerCount);
                    }
                    if (fullProfileResponse.has("following_count") && !fullProfileResponse.get("following_count").isJsonNull()) {
                        int followingCount = fullProfileResponse.get("following_count").getAsInt();
                        followingCountTextView.setText("팔로잉: " + followingCount);
                    }


                    loadUserVideos(currentUsername);

                } else {
                    Toast.makeText(UserProfileActivity.this, "프로필 로딩 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UserProfileActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    // ... rest of the file
    
    
    private void loadUserVideos(String username) {
        RecyclerView recyclerView = findViewById(R.id.user_videos_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Video> userVideos = new ArrayList<>();
        VideoCardAdapter adapter = new VideoCardAdapter(this, userVideos);
        recyclerView.setAdapter(adapter);

        apiService.getVideos(username, null).enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userVideos.clear();
                    userVideos.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(UserProfileActivity.this, "비디오 로딩 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "비디오 로딩 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIDEO_DETAIL_REQUEST_CODE && resultCode == RESULT_OK) {
            loadProfileData();
            return;
        }

        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String newNickname = data.getStringExtra("newNickname");
            String newImageUriString = data.getStringExtra("newImageUri");

            JsonObject updateData = new JsonObject();
            boolean hasNicknameChanged = newNickname != null && !newNickname.equals(currentUsername);

            if (hasNicknameChanged) {
                // Optimistic UI Update for nickname
                currentUsername = newNickname;
                profileNameTextView.setText(currentUsername);

                // Prepare nickname data for backend update
                JsonObject userData = new JsonObject();
                userData.addProperty("first_name", newNickname);
                updateData.add("user", userData);
            }

            if (newImageUriString != null) {
                // A new image was selected, start upload flow
                Uri imageUri = Uri.parse(newImageUriString);
                // Pass the updateData object which may contain nickname changes
                uploadProfileImageToFirebaseAndSaveProfile(imageUri, updateData);
            } else if (hasNicknameChanged) {
                // Only nickname was changed, no new image
                updateProfileOnBackend(updateData);
            } else {
                // No changes detected
                Toast.makeText(UserProfileActivity.this, "수정할 내용이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadProfileImageToFirebaseAndSaveProfile(Uri imageUri, JsonObject updateData) {
        if (imageUri == null) {
            // This case should ideally not be reached if called from onActivityResult correctly
            if (updateData.size() > 0) {
                updateProfileOnBackend(updateData);
            }
            return;
        }

        Toast.makeText(this, "프로필 이미지 업로드 중...", Toast.LENGTH_SHORT).show();

        String fileName = "profile_images/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + UUID.randomUUID().toString();
        storage.getReference().child(fileName).putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            currentProfileImageUrl = downloadUrl; // Update local URL
                            updateData.addProperty("profile_image_url", downloadUrl);
                            updateProfileOnBackend(updateData); // Update backend with all changes
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(UserProfileActivity.this, "이미지 URL 가져오기 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            // If nickname change was also pending, decide if you want to save it anyway
                            // For now, we stop.
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(UserProfileActivity.this, "이미지 업로드 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    // If nickname change was also pending, decide if you want to save it anyway
                    // For now, we stop.
                });
    }

    private void updateProfileOnBackend(JsonObject updateData) {
        apiService.updateProfile(updateData).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserProfileActivity.this, "프로필이 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                    loadProfileData(); // Reload data from server to reflect changes
                } else {
                    Toast.makeText(UserProfileActivity.this, "업데이트 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                 Toast.makeText(UserProfileActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}