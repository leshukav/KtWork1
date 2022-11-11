package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val publish: String,
    val like: Int = 0,
    val share: Int = 990,
    val viewEye: Int = 1,
    val likeByMe: Boolean,
    val video: String
) {
    fun toDto() = Post(id, author, content, publish, like, share, viewEye, likeByMe, video)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.author,
                dto.content,
                dto.publish,
                dto.like,
                dto.share,
                dto.viewEye,
                dto.likeByMe,
                dto.video
            )

    }
}
