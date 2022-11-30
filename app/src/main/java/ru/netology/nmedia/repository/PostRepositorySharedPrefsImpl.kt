package ru.netology.nmedia.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.Post

class PostRepositorySharedPrefsImpl(
    context: Context,
) : PostRepository {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("repo", Context.MODE_PRIVATE)
    private val prefs1 = context.getSharedPreferences("repo1", Context.MODE_PRIVATE)

    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val type1 = TypeToken.getParameterized(List::class.java, Post::class.java).type

    val editor: SharedPreferences.Editor = prefs1.edit()

    private val key = "posts"
    private var nextId = 1L
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        prefs.getString(key, null)?.let {
            posts = gson.fromJson(it, type)
            data.value = posts
        }
    }
    // для презентации убрали пустые строки
    override fun get(): LiveData<List<Post>> = data
    override fun getLastPost(): Post {
        TODO("Not yet implemented")
    }

    override fun save(post: Post) {
        val key = post.author.toString()
        val text = post.content
        with(prefs1.edit()) {
            putString(key, text)
            apply()
        }
        if (post.id == 0L) {
            // TODO: remove hardcoded author & published
            posts = listOf(
                post.copy(
                    id = nextId++,
                    author = "Me",
                    likeByMe = false,
                    publish = "now"
                )
            ) + posts
            data.value = posts
            sync()
            return
        }

        posts = posts.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
        sync()
    }
    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likeByMe = !it.likeByMe,
                like = if (it.likeByMe) it.like - 1 else it.like + 1
            )
        }
        data.value = posts
        sync()
    }

    override fun shareById(id: Long) {
        TODO("Not yet implemented")
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
        sync()
    }
    private fun sync() {
        with(prefs.edit()) {
            putString(key, gson.toJson(posts))
            apply()
        }
    }
}