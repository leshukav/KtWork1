package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorId: Long,
    val authorAvatar: String,
    val content: String,
    val published: Long,
    val likes: Int = 0,
    val likedByMe: Boolean,
    val hidden: Boolean = false,
    @Embedded
    var attachment: Attachment?,

) {
    fun toDto() = Post(id, author, authorId, authorAvatar, content, published,likes,likedByMe,hidden, attachment)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.authorId, dto.authorAvatar, dto.content, dto.published, dto.likes, dto.likedByMe, dto.hidden, dto.attachment)

    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)