
package com.example.health_log

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(isTrainer: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SummarySection()
        Spacer(modifier = Modifier.height(24.dp))
        TabsSection()
        if (isTrainer) {
            Spacer(modifier = Modifier.height(24.dp))
            TrainerStatsSection()
        }
        Spacer(modifier = Modifier.height(24.dp))
        HistorySection()
    }

    // 프로필 편집 버튼 (화면 우측 상단에 배치)
    Box(modifier = Modifier.fillMaxSize()) {
        ProfileEditButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )
    }
}

@Composable
fun SummarySection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background), // 예시 이미지
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("닉네임", fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("업로드한 영상", "작성한 피드백", "받은 채택", "좋아요", "설정")

    Column {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, fontSize = 12.sp) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // 선택된 탭에 따라 다른 콘텐츠 표시
        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
            Text("선택된 탭: ${tabs[selectedTabIndex]}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TrainerStatsSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text("활동 통계 (트레이너)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text("월별 피드백 수: ", fontWeight = FontWeight.Bold)
            Text("123")
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            Text("채택률: ", fontWeight = FontWeight.Bold)
            Text("85%")
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
        LazyColumn(modifier = Modifier.fillMaxWidth().height(200.dp)) { // 높이 제한 예시
            items(historyItems) { item ->
                Text(item, modifier = Modifier.padding(vertical = 8.dp))
                Divider()
            }
        }
    }
}

@Composable
fun ProfileEditButton(modifier: Modifier = Modifier) {
    IconButton(
        onClick = { /* 프로필 편집 화면으로 이동 (동작 비워둠) */ },
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
