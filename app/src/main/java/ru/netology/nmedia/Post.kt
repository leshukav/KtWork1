package ru.netology.nmedia

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val publish: String,
    val like: Int = 0,
    val share: Int = 990,
    val viewEye: Int = 1,
    val likeByMy: Boolean
)
