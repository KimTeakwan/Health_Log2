package com.example.health_log

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.health_log.databinding.ActivityVideoUploadBinding
import com.example.health_log.network.ApiService
import com.example.health_log.network.RetrofitClient
import com.google.firebase.storage.FirebaseStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class VideoUploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoUploadBinding
    private var videoUri: Uri? = null
    private lateinit var apiService: ApiService
    private lateinit var storage: FirebaseStorage

    private val pickVideoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            videoUri = result.data?.data
            if (videoUri != null) {
                binding.videoView.setVideoURI(videoUri)
                binding.videoView.visibility = View.VISIBLE
                binding.videoSelectPrompt.visibility = View.GONE
                binding.videoView.start()
                Toast.makeText(this, "영상이 선택되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = RetrofitClient.getApiService()
        storage = FirebaseStorage.getInstance()

        setupSpinner()
        setupClickListeners()
    }

    private fun setupSpinner() {
        val exerciseTypes = arrayOf("종목 선택", "헬스", "요가", "필라테스", "런닝")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, exerciseTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.exerciseTypeSpinner.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.uploadButton.setOnClickListener {
            uploadVideoToFirebase()
        }

        binding.videoPreviewLayout.setOnClickListener {
            openGalleryForVideo()
        }
    }

    private fun openGalleryForVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        pickVideoLauncher.launch(intent)
    }

    private fun uploadVideoToFirebase() {
        val title = binding.titleEditText.text.toString().trim()
        val description = binding.descriptionEditText.text.toString().trim()
        val tags = binding.tagsEditText.text.toString().trim().split(",").map { it.trim() }

        if (title.isEmpty()) {
            Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (videoUri == null) {
            Toast.makeText(this, "영상을 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Firebase에 업로드 시작...", Toast.LENGTH_SHORT).show()

        val fileName = "videos/${UUID.randomUUID()}"
        val storageRef = storage.reference.child(fileName)

        storageRef.putFile(videoUri!!)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    Log.d("VideoUploadActivity", "Firebase URL: $downloadUrl")
                    createVideoRecordInBackend(title, description, downloadUrl, tags)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Firebase 업로드 실패: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("VideoUploadActivity", "Firebase upload failed", e)
            }
    }

    private fun createVideoRecordInBackend(title: String, description: String, videoUrl: String, tags: List<String>) {
        Toast.makeText(this, "백엔드에 정보 저장 중...", Toast.LENGTH_SHORT).show()

        val request = VideoCreateRequest(
            title = title,
            description = description,
            videoFileUrl = videoUrl,
            tags = tags
        )

        apiService.createVideoRecord(request).enqueue(object : Callback<Video> {
            override fun onResponse(call: Call<Video>, response: Response<Video>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@VideoUploadActivity, "업로드 최종 완료", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@VideoUploadActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(this@VideoUploadActivity, "백엔드 저장 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("VideoUploadActivity", "Backend save failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Video>, t: Throwable) {
                Toast.makeText(this@VideoUploadActivity, "백엔드 저장 중 오류 발생", Toast.LENGTH_SHORT).show()
                Log.e("VideoUploadActivity", "Backend save error", t)
            }
        })
    }
}
