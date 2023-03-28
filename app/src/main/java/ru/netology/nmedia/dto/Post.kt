package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

sealed interface FeedItem{
    val id: Long
}

data class Post(
    override val id: Long,
    val author: String,
    val authorId: Long,
    val authorAvatar: String = "",
    val content: String,
    val published: Long,
    val likes: Int = 0,
    val likedByMe: Boolean,
    val hidden: Boolean = false,
    var attachment: Attachment? = null,
    val ownedByMe:Boolean = false,
): FeedItem
//    val share: Int = 990
//   val viewEye: Int = 1
 //   val video: String = "https://www.youtube.com/watch?v=vJ8unmdwT3M"


data class Ad(
   override val id: Long,
    val image: String,
): FeedItem

data class TimingSeparator(
    override val id: Long,
    val timing: Separator,
): FeedItem

enum class Separator {
    TODAY,
    YERSTUDAY,
    LASTWEEK,
}

data class Attachment(
    val url: String,
    val type: AttachmentType,
)