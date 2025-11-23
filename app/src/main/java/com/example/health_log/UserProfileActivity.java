package com.example.health_log;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

    private ImageView profileImageView;
    private TextView profileNameTextView;
    private TextView profileEmailTextView; // Added for email display
    private TextView adoptedCommentsCountTextView;

    private ApiService apiService;
    private FirebaseStorage storage; // FirebaseStorage instance
    private String currentUsername;
    private String currentProfileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        apiService = RetrofitClient.getApiService();
        storage = FirebaseStorage.getInstance(); // Initialize FirebaseStorage

        profileImageView = findViewById(R.id.profile_image);
        profileNameTextView = findViewById(R.id.profile_name);
        profileEmailTextView = findViewById(R.id.profile_email); // Initialize email TextView
        adoptedCommentsCountTextView = findViewById(R.id.adopted_comments_count);

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
    }

    private void loadProfileData() {
        apiService.getMyProfile().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject fullProfileResponse = response.body().getAsJsonObject();
                    
                    String userRole = null;
                    if (fullProfileResponse.has("role") && !fullProfileResponse.get("role").isJsonNull()) {
                        userRole = fullProfileResponse.get("role").getAsString();
                    }

                    JsonObject profile = null;
                    if (fullProfileResponse.has("profile") && !fullProfileResponse.get("profile").isJsonNull()) {
                        profile = fullProfileResponse.get("profile").getAsJsonObject();
                    }
                    
                    currentUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    if(currentUsername == null || currentUsername.isEmpty()){
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
                    if (profile != null && profile.has("profile_image_url") && !profile.get("profile_image_url").isJsonNull()) {
                        currentProfileImageUrl = profile.get("profile_image_url").getAsString();
                        Glide.with(UserProfileActivity.this)
                             .load(currentProfileImageUrl)
                             .placeholder(R.drawable.ic_person)
                             .error(R.drawable.ic_person)
                             .into(profileImageView);
                    } else {
                        profileImageView.setImageResource(R.drawable.ic_person); // Default image
                        currentProfileImageUrl = null; // Clear if no URL from backend
                    }
                    
                    if ("trainer".equals(userRole) && profile != null && profile.has("adopted_comment_count") && !profile.get("adopted_comment_count").isJsonNull()) {
                        int adoptedCommentCount = profile.get("adopted_comment_count").getAsInt();
                        adoptedCommentsCountTextView.setText("채택된 댓글: " + adoptedCommentCount + "개");
                        adoptedCommentsCountTextView.setVisibility(View.VISIBLE);
                    } else {
                        adoptedCommentsCountTextView.setVisibility(View.GONE);
                    }

                    loadUserVideos(currentUsername);

                } else {
                    Toast.makeText(UserProfileActivity.this, "프로필 로딩 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
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
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String newNickname = data.getStringExtra("newNickname");
            String newImageUriString = data.getStringExtra("newImageUri");

            if (newImageUriString != null) {
                Uri imageUri = Uri.parse(newImageUriString);
                uploadProfileImageToFirebaseAndSaveProfile(imageUri);
            } else {
                // If no new image, but other data was edited, update profile without image upload
                // Currently, newNickname is not sent to backend easily
                Map<String, Object> updateData = new HashMap<>();
                // Potentially add newNickname to updateData if backend supported
                if (!updateData.isEmpty()) {
                    updateProfileOnBackend(updateData);
                } else {
                    Toast.makeText(UserProfileActivity.this, "수정할 내용이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadProfileImageToFirebaseAndSaveProfile(Uri imageUri) {
        if (imageUri == null) {
            Toast.makeText(this, "업로드할 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "프로필 이미지 업로드 중...", Toast.LENGTH_SHORT).show();

        String fileName = "profile_images/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + UUID.randomUUID().toString();
        storage.getReference().child(fileName).putFile(imageUri)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storage.getReference().child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();
                            currentProfileImageUrl = downloadUrl; // Update local URL
                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("profile_image_url", downloadUrl);
                            updateProfileOnBackend(updateData);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserProfileActivity.this, "이미지 URL 가져오기 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserProfileActivity.this, "이미지 업로드 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }

    private void updateProfileOnBackend(Map<String, Object> updateData) {
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