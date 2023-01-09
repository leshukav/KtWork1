package ru.netology.nmedia

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: Long,
    val likes: Int = 0,
    val likedByMe: Boolean,
) {
    val share: Int = 990
    val viewEye: Int = 1
    val video: String = "https://www.youtube.com/watch?v=vJ8unmdwT3M"
}
