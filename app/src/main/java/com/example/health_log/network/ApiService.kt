package com.example.health_log.network

import com.example.health_log.Comment
import com.example.health_log.TrainerProfile
import com.example.health_log.Video
import com.example.health_log.VideoCreateRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("videos/")
    fun getVideos(@Query("tag") tag: String?): Call<List<Video>>

    @POST("videos/")
    fun createVideoRecord(@Body request: VideoCreateRequest): Call<Video>

    @GET("videos/{id}/")
    fun getVideo(@Path("id") videoId: Int): Call<Video>

    @POST("videos/{id}/like/")
    fun likeVideo(@Path("id") videoId: Int): Call<Void>

    @POST("videos/{id}/comments/")
    fun postComment(@Path("id") videoId: Int, @Body comment: Comment): Call<Comment>

    @PUT("comments/{id}/adopt/")
    fun adoptComment(@Path("id") commentId: Int): Call<Void>

    @GET("users/profile/")
    fun getTrainerProfile(): Call<TrainerProfile>
}
