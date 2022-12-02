package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var posts = List(10) {
        Post(
            id = it.toLong() + 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "№${it + 1}  Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу.  Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            publish = "21 мая в 18:36",
            likeByMe = false,
            video = "https://www.youtube.com/watch?v=vJ8unmdwT3M"
        )
    }
    private val data = MutableLiveData(posts)

    override fun get(): LiveData<List<Post>> = data
    override fun getLastPost(): Post {
        TODO("Not yet implemented")
    }

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else if (!it.likeByMe) {
                it.copy(like = it.like + 1, likeByMe = !it.likeByMe)
            } else {
                it.copy(like = it.like - 1, likeByMe = !it.likeByMe)
            }
        }
        data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(share = it.share + 10)
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {
        val nextId = posts.size.toLong()
        posts = if (post.id == 0L) {
            listOf(
                post.copy(
                    id = nextId + 1,
                    author = "Me",
                    likeByMe = false,
                    publish = "now",
                    video = "https://www.youtube.com/watch?v=vJ8unmdwT3M"
                )
            ) + posts
        } else {
            posts.map {
                if (it.id != post.id) it else it.copy(content = post.content)
            }
        }
        data.value = posts
    }
}
