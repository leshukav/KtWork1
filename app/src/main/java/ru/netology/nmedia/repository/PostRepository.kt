package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.Post

interface PostRepository {
    fun get():List<Post>
 //   fun getLastPost(): Post
    fun likeById(id: Long) : Post
    fun unlikeById(id: Long) : Post
    fun shareById(id: Long)
    fun removeById(id: Long)
    fun save(post: Post)

}
