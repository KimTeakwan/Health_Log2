package com.example.health_log

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.health_log.databinding.ActivityVideoUploadBinding

class VideoUploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoUploadBinding

    // 갤러리에서 영상을 선택하고 그 결과를 처리하는 런처
    private val pickVideoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val videoUri: Uri? = result.data?.data
            if (videoUri != null) {
                // 비디오 URI를 받아서 처리 (예: VideoView에 설정)
                binding.videoView.setVideoURI(videoUri)
                binding.videoView.visibility = View.VISIBLE
                binding.videoSelectPrompt.visibility = View.GONE
                binding.videoView.start() // 자동 재생
                Toast.makeText(this, "영상이 선택되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinner()
        setupClickListeners()
    }

    private fun setupSpinner() {
        // 실제 운동 종목으로 교체해야 합니다.
        val exerciseTypes = arrayOf("종목 선택", "헬스", "요가", "필라테스", "런닝")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, exerciseTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.exerciseTypeSpinner.adapter = adapter
    }

    private fun setupClickListeners() {
        // 뒤로 가기 버튼
        binding.backButton.setOnClickListener {
            Toast.makeText(this, "업로드를 취소하고 이전 화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show()
            finish() // 현재 액티비티 종료
        }

        // 업로드 버튼
        binding.uploadButton.setOnClickListener {
            // TODO: 실제 업로드 로직 구현
            val title = binding.titleEditText.text.toString()
            if (title.isEmpty()) {
                Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "'$title' 영상을 업로드합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 영상 선택 영역
        binding.videoPreviewLayout.setOnClickListener {
            openGalleryForVideo()
        }
    }

    private fun openGalleryForVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        pickVideoLauncher.launch(intent)
    }
}
