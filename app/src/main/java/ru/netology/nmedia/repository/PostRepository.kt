package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.MediaModel

interface PostRepository {
    val data: Flow<List<Post>>
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun unreadCount(): Int
    suspend fun getAll()
    suspend fun loadNewer()
    suspend fun refresh()
    suspend fun saveWithAttachment(post: Post, media: MediaModel)
    suspend fun upload(upload: MediaModel): Media
    suspend fun getById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun unlikeById(id: Long)
    suspend fun shareById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
    suspend fun saveOld(post: Post)
    suspend fun cancelLike(id: Long)
    suspend fun authorization(login: String, pass: String)
    suspend fun registration(name: String, login: String, pass: String)
}
