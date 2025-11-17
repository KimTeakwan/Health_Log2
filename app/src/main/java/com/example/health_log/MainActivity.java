package com.example.health_log;

import android.content.Intent;
import android.content.SharedPreferences; // ✅ 이 줄이 추가되었습니다.
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Video> videoList = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        tags.add("Chest");
        tags.add("Push");

        // ✅ Video 객체 생성 로직
        for (int i = 0; i < 20; i++) {
            // 1. 필수 정보(제목, 태그, 설명)로 객체 생성
            Video video = new Video(
                    "Video Title " + (i + 1), // title
                    tags,                     // tags
                    "This is a description."  // description
            );

            // 2. 나머지 정보는 setter를 이용해 설정
            video.setUploader("Uploader " + (i + 1));
            video.setLikes(1200 + i);
            video.setComments(345 + i);
            video.setThumbnailUrl("");

            videoList.add(video);
        }

        VideoCardAdapter adapter = new VideoCardAdapter(videoList);
        recyclerView.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, VideoUploadActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile) {
            // ✅ SharedPreferences 사용 시 import가 필요했음
            SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
            String userType = sharedPreferences.getString("user_type", "user"); // Default to "user"

            Intent intent;
            if ("trainer".equals(userType)) {
                intent = new Intent(MainActivity.this, ProfileActivity.class);
            } else {
                intent = new Intent(MainActivity.this, UserProfileActivity.class);
            }
            startActivity(intent);
            return true;
        } else if (id == R.id.action_toggle_theme) {
            int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}