package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: Long,
    val likes: Int = 0,
    val likedByMe: Boolean,
    val hidden: Boolean = false,
    @Embedded
    var attachment: Attachment?,

) {
    fun toDto() = Post(id, author, authorAvatar, content, published,likes,likedByMe,hidden, attachment)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.authorAvatar, dto.content, dto.published, dto.likes, dto.likedByMe, dto.hidden, dto.attachment)

    }
}

//data class Attachment(
//    var url: String,
//    var type: AttachmentType,
//) {
//    fun toDto() = Attachment(url, type)
//
//    companion object {
//        fun fromDto(dto: Attachment?) = dto?.let {
//            Attachment(it.url, it.type)
//        }
//    }
//}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)