package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var posts = List(50) {
        Post(
            id = it.toLong(),
            author = "Нетология. Университет интернет-профессий будущего",
            content = "№$it  Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу.  Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            publish = "21 мая в 18:36",
            likeByMy = false
        )
    }
    private val data = MutableLiveData(posts)

    override fun get(): LiveData<List<Post>> = data
    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else if (!it.likeByMy) {
                it.copy(like = it.like + 1, likeByMy = !it.likeByMy)
            } else {
                it.copy(like = it.like - 1, likeByMy = !it.likeByMy)
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

}
