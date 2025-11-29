package com.example.health_log;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.health_log.model.SimpleUser;
import com.example.health_log.network.ApiService;
import com.example.health_log.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowListActivity extends AppCompatActivity {

    public static final String EXTRA_USER_ID = "USER_ID";
    public static final String EXTRA_LIST_TYPE = "LIST_TYPE";

    private RecyclerView recyclerView;
    private FollowListAdapter adapter;
    private List<SimpleUser> userList;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.follow_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        adapter = new FollowListAdapter(this, userList);
        recyclerView.setAdapter(adapter);

        apiService = RetrofitClient.getApiService();

        String userId = getIntent().getStringExtra(EXTRA_USER_ID);
        String listType = getIntent().getStringExtra(EXTRA_LIST_TYPE);

        if (userId == null || listType == null) {
            Toast.makeText(this, "Error: User ID or list type missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if ("followers".equals(listType)) {
            getSupportActionBar().setTitle("Followers");
            fetchFollowers(userId);
        } else {
            getSupportActionBar().setTitle("Following");
            fetchFollowing(userId);
        }
    }

    private void fetchFollowers(String userId) {
        apiService.getFollowers(userId).enqueue(new Callback<List<SimpleUser>>() {
            @Override
            public void onResponse(Call<List<SimpleUser>> call, Response<List<SimpleUser>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(FollowListActivity.this, "Failed to load followers.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SimpleUser>> call, Throwable t) {
                Toast.makeText(FollowListActivity.this, "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFollowing(String userId) {
        apiService.getFollowing(userId).enqueue(new Callback<List<SimpleUser>>() {
            @Override
            public void onResponse(Call<List<SimpleUser>> call, Response<List<SimpleUser>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(FollowListActivity.this, "Failed to load following list.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SimpleUser>> call, Throwable t) {
                Toast.makeText(FollowListActivity.this, "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
