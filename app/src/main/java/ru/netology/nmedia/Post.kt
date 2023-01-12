package ru.netology.nmedia

import ru.netology.nmedia.enumeration.AttachmentType

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String = "",
    val content: String,
    val published: Long,
    val likes: Int = 0,
    val likedByMe: Boolean,
    var attachment: Attachment? = null,
) {
    val share: Int = 990
    val viewEye: Int = 1
    val video: String = "https://www.youtube.com/watch?v=vJ8unmdwT3M"
}


data class Attachment(
    val url: String,
    val description: String?,
    val type: AttachmentType,
)