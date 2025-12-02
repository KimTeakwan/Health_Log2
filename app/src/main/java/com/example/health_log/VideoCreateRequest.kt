package com.example.health_log

import com.google.gson.annotations.SerializedName

data class VideoCreateRequest(
    val title: String,
    val description: String,
    @SerializedName("video_file")
    val videoFileUrl: String,
    @SerializedName("tag_names")
    val tag_names: List<String>,
    @SerializedName("requests_feedback")
    val requestsFeedback: Boolean,
    val visibility: String
)