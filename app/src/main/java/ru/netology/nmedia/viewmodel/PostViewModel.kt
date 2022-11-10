package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.netology.nmedia.Post
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositorySQLiteImpl

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likeByMe = false,
    publish = "",
    share = 0,
    video = "https://www.youtube.com/watch?v=vJ8unmdwT3M"
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositorySQLiteImpl(
        AppDb.getInstance(application).postDao
    )
        //PostRepositoryFileImpl(application)
    val data = repository.get()
    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)

    val edited = MutableLiveData(empty)

    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    fun cancelEdit() {
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContentAndSave(content: String) {
        edited.value?.let { it ->
            val text = content.trim()
            if (it.content == text) {
                return
            }
            edited.value?.let {
                repository.save(it.copy(content = text))
            }
            edited.value = empty
        }

    }
}