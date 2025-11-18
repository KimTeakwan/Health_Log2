package com.example.health_log

import com.google.gson.annotations.SerializedName

data class VideoCreateRequest(
    val title: String,
    val description: String,
    @SerializedName("video_file")
    val videoFileUrl: String,
    val tags: List<String>
)
