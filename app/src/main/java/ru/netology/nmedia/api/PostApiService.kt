package ru.netology.nmedia.api

import android.net.Uri
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Param
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.Recipient
import ru.netology.nmedia.model.AuthModel

private const val BASE_URL = "http://10.0.2.2:9999/api/"

private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

private val okhttp = OkHttpClient.Builder()
    .addInterceptor(logging)
    .addInterceptor {chain ->
        val request = AppAuth.getInstance().data.value?.token?.let {
            chain.request().newBuilder()
                .addHeader("Authorization", it)
                .build()
        } ?: chain.request()
        chain.proceed(request)
    }
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okhttp)
    .build()

interface PostsApiService {
    @GET("slow/posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("slow/posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

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
    suspend fun checkRecipientId(@Query("token") token: String, @Body recipient: Recipient)//: Response<Recipient>
}

object PostsApi {
    val retrofitService: PostsApiService by lazy {
        retrofit.create(PostsApiService::class.java)
    }
}