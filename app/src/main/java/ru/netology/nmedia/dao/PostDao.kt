
package ru.netology.nmedia.dao

import ru.netology.nmedia.Post

interface PostDao {
    fun get(): List<Post>
    fun save(post: Post): Post
    fun likeById(id: Long)
    fun shareById(id: Long)
    fun removeById(id: Long)
}