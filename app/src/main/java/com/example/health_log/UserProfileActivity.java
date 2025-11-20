package com.example.health_log;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.health_log.network.ApiService;
import com.example.health_log.network.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private static final int EDIT_PROFILE_REQUEST_CODE = 200;

    private ImageView profileImageView;
    private TextView profileNameTextView;
    private String imageUriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        profileImageView = findViewById(R.id.profile_image);
        profileNameTextView = findViewById(R.id.profile_name);

        loadProfileData();

        Button logoutButton = findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(UserProfileActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        Button editProfileButton = findViewById(R.id.btn_edit_profile);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("nickname", profileNameTextView.getText().toString());
                intent.putExtra("imageUri", imageUriString);
                startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
            }
        });

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.user_videos_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Video> userVideos = new ArrayList<>();
        VideoCardAdapter adapter = new VideoCardAdapter(this, userVideos);
        recyclerView.setAdapter(adapter);

        // Fetch videos and filter for the current user
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getVideos(null).enqueue(new retrofit2.Callback<List<Video>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Video>> call, retrofit2.Response<List<Video>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String currentUserName = profileNameTextView.getText().toString();
                    userVideos.clear();
                    for (Video video : response.body()) {
                        if (video.getUploader() != null && currentUserName.equals(video.getUploader().getUsername())) {
                            userVideos.add(video);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(UserProfileActivity.this, "Failed to load videos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Video>> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Error loading videos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProfileData() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String nickname = sharedPreferences.getString("user_nickname", "김일반");
        imageUriString = sharedPreferences.getString("user_image_uri", null);

        profileNameTextView.setText(nickname);
        if (imageUriString != null) {
            profileImageView.setImageURI(Uri.parse(imageUriString));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String newNickname = data.getStringExtra("newNickname");
            String newImageUriString = data.getStringExtra("newImageUri");

            SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_nickname", newNickname);
            if (newImageUriString != null) {
                editor.putString("user_image_uri", newImageUriString);
            }
            editor.apply();

            // Reload data into UI
            loadProfileData();
        }
    }
}
