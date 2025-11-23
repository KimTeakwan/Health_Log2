package com.example.health_log;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.health_log.network.ApiService;
import com.example.health_log.network.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {

    private static final int EDIT_PROFILE_REQUEST_CODE = 200;

    private ImageView profileImageView;
    private TextView profileNameTextView;

    private ApiService apiService;
    private String currentUsername;
    private String currentProfileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        apiService = RetrofitClient.getApiService();

        profileImageView = findViewById(R.id.profile_image);
        profileNameTextView = findViewById(R.id.profile_name);

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
                    JsonObject profile = response.body().getAsJsonObject();
                    
                    // The actual username is part of the user object, not the profile object.
                    // We need to fetch it separately or the API needs to be adjusted.
                    // For now, let's assume the API returns what we need.
                    // A better API would return the full user object including username.
                    // Let's assume another call to get the user object for now.
                    // This highlights a design flaw in the API. We'll patch it on the client for now.
                    
                    // We will get username from firebase auth for now as a workaround
                    currentUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    if(currentUsername == null || currentUsername.isEmpty()){
                       // Fallback if display name is not set
                       currentUsername = "My Profile";
                    }

                    profileNameTextView.setText(currentUsername);

                    if (profile.has("profile_image_url") && !profile.get("profile_image_url").isJsonNull()) {
                        currentProfileImageUrl = profile.get("profile_image_url").getAsString();
                        Glide.with(UserProfileActivity.this)
                             .load(currentProfileImageUrl)
                             .placeholder(R.drawable.ic_person) //
                             .error(R.drawable.ic_person)     //
                             .into(profileImageView);
                    }
                    
                    // After loading profile, load the user's videos
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

        apiService.getVideos(username).enqueue(new Callback<List<Video>>() {
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

            Map<String, Object> updateData = new HashMap<>();
            
            // Note: The backend doesn't support changing the username (nickname) easily.
            // We will only update the profile image for now.
            if (newImageUriString != null) {
                // TODO: Here you would upload the file from newImageUriString to Firebase Storage,
                // get the download URL, and put that URL in the map.
                // For now, we'll just put the (local) URI string as a placeholder.
                updateData.put("profile_image_url", newImageUriString);
            }

            if (updateData.isEmpty()) {
                return; // Nothing to update
            }
            
            apiService.updateProfile(updateData).enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(UserProfileActivity.this, "프로필이 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                        // Reload data from server to reflect changes
                        loadProfileData();
                    } else {
                        Toast.makeText(UserProfileActivity.this, "업데이트 실패", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                     Toast.makeText(UserProfileActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}