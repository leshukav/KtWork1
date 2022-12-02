package ru.netology.nmedia.repository

import androidx.lifecycle.Transformations
import ru.netology.nmedia.Post
import ru.netology.nmedia.dao.PostDaoRoom
import ru.netology.nmedia.entity.PostEntity

class PostRepositoryImpl(
    private val dao: PostDaoRoom,

    ) : PostRepository {

    override fun get() = Transformations.map(dao.get()) { list ->
        list.map {
            it.toDto()
        }
    }

    override fun getLastPost() = dao.getLastPost().toDto() // : Post {

    override fun likeById(id: Long) {
        dao.likeById(id)
    }

    override fun shareById(id: Long) {
        dao.shareById(id)
    }

    override fun save(post: Post) {
        dao.save(PostEntity.fromDto(post))
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
    }

}