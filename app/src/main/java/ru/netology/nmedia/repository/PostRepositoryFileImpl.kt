package ru.netology.nmedia.repository

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.Post

class PostRepositoryFileImpl(application: Application) : PostRepository {


    private val gson = Gson()
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val filename = "posts1.json"
//    private var posts = List(10) {
//        Post(
//            id = it.toLong() + 1,
//            author = "Нетология. Университет интернет-профессий будущего",
//            content = "№${it + 1}  Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу.  Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
//            publish = "21 мая в 18:36",
//            likeByMy = false
//        )
//    }
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

//    init {
//        val file = context.filesDir.resolve(filename)
//        if (file.exists()) {
//            // если файл есть - читаем
//            context.openFileInput(filename).bufferedReader().use {
//                posts = gson.fromJson(it, type)
//                data.value = posts
//            }
//        } else {
//            // если нет, записываем пустой массив
//            sync()
//        }
//    }

    override fun get(): LiveData<List<Post>> = data
    override fun getLastPost(): Post {
        TODO("Not yet implemented")
    }

    override fun save(post: Post) {
        val nextId = posts.size.toLong()
        posts = if (post.id == 0L) {
            listOf(
                post.copy(
                    id = nextId + 1,
                    author = "Me",
                    likeByMe = false,
                    publish = "now"
                )
            ) + posts
        } else {
            posts.map {
                if (it.id != post.id) it else it.copy(content = post.content)
            }
        }
        data.value = posts
   //     sync()
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
 //       sync()
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
  //      sync()
    }

//    private fun sync() {
//        context.openFileOutput(filename, Context.MODE_PRIVATE).bufferedWriter().use {
//            it.write(gson.toJson(posts))
//        }
//    }
}