package com.example.health_log.network

import com.example.health_log.Comment
import com.example.health_log.PublicProfileActivity
import com.example.health_log.SignUpRequest // From .kt
import com.example.health_log.TrainerProfile // From .kt
import com.example.health_log.Video
import com.example.health_log.VideoCreateRequest
import com.google.gson.JsonElement // From .java
import com.google.gson.JsonObject // Add this import
import com.example.health_log.model.SimpleUser

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH // From .java
import retrofit2.http.POST
import retrofit2.http.PUT // From .kt (for adoptComment - but backend uses POST)
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Multipart // From .kt
import retrofit2.http.Part // From .kt
import okhttp3.MultipartBody // From .kt
import okhttp3.RequestBody // From .kt
import com.example.health_log.ReportRequestBody

// Reconciled ApiService interface
interface ApiService {

    @POST("signup/")
    fun signup(@Body request: SignUpRequest): Call<Void>

    // Backend uses 'q' for general search, not 'tag'. Modified from .kt, matched to .java
    @GET("videos/")
    fun getVideos(@Query("q") searchQuery: String?, @Query("sort_by") sortBy: String?): Call<List<Video>>

    @POST("videos/")
    fun createVideoRecord(@Body request: VideoCreateRequest): Call<Video>

    @GET("videos/{id}/")
    fun getVideo(@Path("id") videoId: Int): Call<Video>

    @POST("videos/{pk}/likes/")
    fun likeVideo(@Path("pk") videoId: Int): Call<Void>
    
    @DELETE("videos/{pk}/likes/")
    fun unlikeVideo(@Path("pk") videoId: Int): Call<Void>

    @POST("videos/{pk}/comments/") // Use pk consistently. Matched to .java
    fun postComment(@Path("pk") videoId: Int, @Body comment: Comment): Call<Comment>

    // Backend uses POST for adoptComment. Modified from .kt, matched to .java
    @POST("comments/{pk}/adopt/")
    fun adoptComment(@Path("pk") commentId: Int): Call<Void>

    @POST("reports/")
    fun reportContent(@Body body: com.example.health_log.ReportBody): Call<Void>

    // From .java (for PublicProfileActivity)
    @GET("users/{pk}/")
    fun getUserProfile(@Path("pk") userId: Int): Call<PublicProfileActivity.PublicProfileResponse>

    // From .java (for Follow/Unfollow)
    @POST("users/{pk}/follow/")
    fun followToggle(@Path("pk") userId: Int): Call<Void>

    @GET("users/{pk}/followers/")
    fun getFollowers(@Path("pk") userId: String): Call<List<SimpleUser>>

    @GET("users/{pk}/following/")
    fun getFollowing(@Path("pk") userId: String): Call<List<SimpleUser>>

    // From .java (for UserProfileActivity)
    @GET("profile/")
    fun getMyProfile(): Call<JsonElement>

    // From .java (for UserProfileActivity update)
    @PATCH("profile/")
    fun updateProfile(@Body data: JsonObject): Call<JsonElement>

    // Original .kt method. Specific to TrainerProfile.
    @GET("users/profile/")
    fun getTrainerProfile(): Call<TrainerProfile>

    // Original .kt Multipart methods. Unclear what they are for, but keeping them.
    @Multipart
    @PUT("users/profile_image/")
    fun uploadProfileImage(
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<JsonElement>
}