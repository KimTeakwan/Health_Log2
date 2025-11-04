package com.example.health_log;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatDelegate;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        androidx.recyclerview.widget.RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        java.util.List<Video> videoList = new java.util.ArrayList<>();
        java.util.List<String> tags = new java.util.ArrayList<>();
        tags.add("Chest");
        tags.add("Push");


        for (int i = 0; i < 20; i++) {
            // Video(String, List<String>, String, String) 생성자에 맞게 수정
            // 순서: (제목, 태그, 업로더, 설명)으로 가정
            Video video = new Video(
                    "Video Title " + (i + 1),      // 1. String (제목)
                    tags,                          // 2. List<String> (태그)
                    "Uploader " + (i + 1),         // 3. String (업로더)
                    "이것은 동영상 설명입니다. " + (i + 1) // 4. String (설명)
            );

            // 좋아요나 댓글 수 등은 setter를 사용해 별도로 값을 넣어줄 수 있습니다.
            // video.setLikes(1200 + i);
            // video.setComments(345 + i);

            videoList.add(video);
        }

        VideoCardAdapter adapter = new VideoCardAdapter(videoList);
        recyclerView.setAdapter(adapter);

        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search submission
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search query text change
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_theme) {
            int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
            }
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}