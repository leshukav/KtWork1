package ru.netology.nmedia.repository

import ru.netology.nmedia.Post

interface PostRepository {
    fun get():List<Post>
    fun likeById(id: Long, callback: GetAllCallback<Post>)
    fun unlikeById(id: Long, callback: GetAllCallback<Post>)
    fun shareById(id: Long, callback: GetAllCallback<Unit>)
    fun removeById(id: Long, callback: GetAllCallback<Unit>)
    fun save(post: Post, callback: GetAllCallback<Post>)

    fun getAllAsync(callback: GetAllCallback<List<Post>>)

    interface GetAllCallback<T> {
        fun onSuccess(data : T) {}
        fun onError(e: Exception) {}
    }
}
