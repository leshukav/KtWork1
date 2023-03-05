package ru.netology.nmedia.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.Recipient
import ru.netology.nmedia.model.AuthModel

interface PostsApiService {
    @GET("slow/posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("slow/posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>

    @GET("slow/posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    @GET("slow/posts/{id}/newer")
    suspend fun getBefore(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>

    @GET("slow/posts/{id}/newer")
    suspend fun getAfter(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>

    @Multipart
    @POST("slow/media")
    suspend fun upload(@Part media: MultipartBody.Part): Response<Media>

    @GET("slow/posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("slow/posts")
    suspend fun save(@Body post: Post): Response<Post>

    @DELETE("slow/posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("slow/posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("slow/posts/{id}/likes")
    suspend fun unlikeById(@Path("id") id: Long): Response<Post>

    @FormUrlEncoded
    @POST("slow/users/authentication")
    suspend fun updateUser(@Field("login") login: String, @Field("pass") pass: String): Response<AuthModel>

    @FormUrlEncoded
    @POST("slow/users/registration")
    suspend fun registerUser(@Field("login") login: String, @Field("pass") pass: String, @Field("name") name: String): Response<AuthModel>

    @POST("slow/users/push-tokens")
    suspend fun sendPushToken(@Body token: PushToken): Response<Unit>

    @POST("pushes")
    suspend fun checkRecipientId(@Query("token") token: String, @Body recipient: Recipient)
}