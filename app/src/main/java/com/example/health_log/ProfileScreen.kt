package com.example.health_log

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.health_log.network.RetrofitClient

// ✅ 중요: TrainerProfile이 정의된 패키지를 정확히 Import 해야 합니다.
// (현재 파일과 같은 패키지에 있다면 이 줄은 없어도 되지만, 다른 패키지라면 확인 필요)
import com.example.health_log.TrainerProfile

// ✅ Retrofit 필수 Import
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ProfileScreen(isTrainer: Boolean) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    // RetrofitClient는 remember로 감싸지 않아도 되지만, 재생성을 막기 위해 remember 사용 가능
    val apiService = remember { RetrofitClient.getApiService() }

    var nickname by remember {
        mutableStateOf(sharedPreferences.getString("trainer_nickname", "김트레이너") ?: "김트레이너")
    }
    var imageUri by remember {
        mutableStateOf(sharedPreferences.getString("trainer_image_uri", null))
    }
    var trainerProfile by remember { mutableStateOf<TrainerProfile?>(null) }

    // ✅ API 호출 로직 수정 (오류 해결의 핵심 부분)
    LaunchedEffect(isTrainer) {
        if (isTrainer) {
            // 1. Call 객체 생성 (타입 명시: Call<TrainerProfile>)
            val call: Call<TrainerProfile> = apiService.getTrainerProfile()

            // 2. enqueue 실행 (람다식 {} 대신 object : Callback<T> 사용)
            call.enqueue(object : Callback<TrainerProfile> {
                override fun onResponse(
                    call: Call<TrainerProfile>,
                    response: Response<TrainerProfile>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            trainerProfile = body
                            // User 객체 null 체크 후 닉네임 업데이트
                            val fetchedName = body.user?.getUsername()
                            if (!fetchedName.isNullOrEmpty()) {
                                nickname = fetchedName
                            }
                        }
                    } else {
                        Log.e("ProfileScreen", "API 실패 코드: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<TrainerProfile>, t: Throwable) {
                    Log.e("ProfileScreen", "통신 에러: ${t.message}", t)
                }
            })
        }
    }

    // 프로필 편집 Activity 결과 처리
    val editProfileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val newNickname = data?.getStringExtra("newNickname")
            val newImageUri = data?.getStringExtra("newImageUri")

            if (newNickname != null) {
                nickname = newNickname
                sharedPreferences.edit().putString("trainer_nickname", newNickname).apply()
            }
            if (newImageUri != null) {
                imageUri = newImageUri
                sharedPreferences.edit().putString("trainer_image_uri", newImageUri).apply()
            }
        }
    }

    // UI 레이아웃
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            SummarySection(nickname, imageUri)
            Spacer(modifier = Modifier.height(24.dp))
            TabsSection()

            if (isTrainer) {
                Spacer(modifier = Modifier.height(24.dp))
                // trainerProfile 데이터가 로드되었을 때만 표시
                trainerProfile?.let { TrainerStatsSection(it) }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HistorySection()
        }

        // 우측 상단 편집 버튼
        ProfileEditButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            val intent = Intent(context, EditProfileActivity::class.java).apply {
                putExtra("nickname", nickname)
                putExtra("imageUri", imageUri)
            }
            editProfileLauncher.launch(intent)
        }
    }
}

@Composable
fun SummarySection(nickname: String, imageUri: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        AsyncImage(
            model = imageUri ?: R.drawable.ic_person, // res/drawable/ic_person.xml이 있어야 함
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(nickname, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("배지 레벨: 마스터", fontSize = 14.sp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("팔로워", fontWeight = FontWeight.Bold)
            Text("1,234")
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("포인트", fontWeight = FontWeight.Bold)
            Text("5,678P")
        }
    }
}

@Composable
fun TabsSection() {
    // 탭 선택 상태 관리 (mutableIntStateOf 사용 권장)
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("업로드한 영상", "작성한 피드백", "받은 채택", "좋아요", "설정")

    Column {
        // 탭이 많아 화면을 넘어갈 수 있으므로 ScrollableTabRow 사용
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 0.dp, // 시작 부분 패딩 제거
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, fontSize = 12.sp) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("선택된 탭: ${tabs[selectedTabIndex]}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TrainerStatsSection(trainerProfile: TrainerProfile) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text("활동 통계 (트레이너)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text("월별 피드백 수: ", fontWeight = FontWeight.Bold)
            Text("123") // 실제 데이터가 있다면 trainerProfile.monthlyFeedbackCount 등으로 교체
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            Text("채택된 댓글 수: ", fontWeight = FontWeight.Bold)
            // TrainerProfile 데이터 클래스의 필드명과 일치해야 합니다.
            Text(trainerProfile.adoptedCommentCount.toString())
        }
    }
}

@Composable
fun HistorySection() {
    val historyItems = listOf(
        "마스터 배지 획득 (2024-10-28)",
        "프로 배지 획득 (2024-08-15)",
        "비기너 배지 획득 (2024-07-01)"
    )
    Column {
        Text("배지/레벨 히스토리", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            items(historyItems) { item ->
                Text(item, modifier = Modifier.padding(vertical = 8.dp))
                HorizontalDivider() // Material3에서는 Divider 대신 HorizontalDivider 권장
            }
        }
    }
}

@Composable
fun ProfileEditButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(Icons.Default.Edit, contentDescription = "프로필 편집")
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenTrainerPreview() {
    MaterialTheme {
        ProfileScreen(isTrainer = true)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenUserPreview() {
    MaterialTheme {
        ProfileScreen(isTrainer = false)
    }
}