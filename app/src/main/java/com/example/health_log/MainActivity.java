package com.example.health_log;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.health_log.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // 1. 뷰 바인딩 객체와 어댑터 변수 선언
    private ActivityMainBinding binding;
    private VideoCardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 2. 뷰 바인딩을 사용하여 레이아웃 설정
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 3. 툴바 설정
        setSupportActionBar(binding.toolbar);

        // 4. RecyclerView 설정
        setupRecyclerView();

        // 5. SearchView 설정
        setupSearchView();

        // 6. FloatingActionButton 클릭 리스너 설정
        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, VideoUploadActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        // RecyclerView 레이아웃 매니저 설정
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // RecyclerView 아이템 크기가 고정되어 있으면 성능 최적화를 위해 이 옵션을 추가
        binding.recyclerView.setHasFixedSize(true);

        // 6. 별도 메서드에서 더미 데이터 생성
        List<Video> videoList = createDummyVideoData();

        // 어댑터 생성 및 RecyclerView에 연결
        adapter = new VideoCardAdapter(videoList);
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 검색 버튼을 눌렀을 때의 동작 (보통 실시간 검색을 사용하므로 비워둠)
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 7. 텍스트가 변경될 때마다 어댑터의 필터링 메서드 호출
                if (adapter != null) {
                    adapter.filter(newText);
                }
                return true;
            }
        });
    }

    // 8. 더미 데이터 생성 로직을 별도 메서드로 분리
    private List<Video> createDummyVideoData() {
        List<Video> videoList = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        tags.add("가슴");
        tags.add("푸쉬");
        tags.add("헬스");

        // Video 생성자에 맞게 수정 (title, tags, description)
        for (int i = 0; i < 20; i++) {
            Video video = new Video(
                    "헬스 영상 제목 " + (i + 1),
                    tags,
                    "이 영상은 " + (i + 1) + "번째 더미 데이터입니다."
            );
            // Setter를 사용하여 추가 정보 설정
            video.setUploader("헬스 유튜버 " + (i + 1));
            video.setLikes(1200 + i * 10);
            video.setComments(345 + i * 5);
            // 날짜 정보는 Video 생성자에서 자동으로 new Date()로 생성되므로 별도 설정 불필요
            videoList.add(video);
        }
        return videoList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_theme) {
            // 현재 테마 모드 확인
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            // 다크 모드와 라이트 모드 전환
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            // 액티비티를 다시 생성하여 테마를 즉시 적용
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
