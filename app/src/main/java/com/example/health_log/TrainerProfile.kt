package com.example.health_log

import com.google.gson.annotations.SerializedName

data class TrainerProfile(
    val user: User,
    val specialty: String,
    val certification: String,
    @SerializedName("adopted_comment_count")
    val adoptedCommentCount: Int
)
