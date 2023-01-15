package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import ru.netology.nmedia.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent


private val empty = Post(
    id = 0,
    content = "",
    author = "Pushkin",
    likedByMe = false,
    published = 26122022,
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data

    val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.GetAllCallback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.value = (FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.value = (FeedModel(error = true))
            }
        })
    }

    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        _data.value = _data.value?.copy(posts = _data.value?.posts.orEmpty()
                .filter { it.id != id }
        )
        repository.removeById(id, object : PostRepository.GetAllCallback<Unit> {
            override fun onSuccess(data: Unit) {

            }

            override fun onError(e: Exception) {
                _data.value =_data.value?.copy(posts = old)
            }
        })

    }

    fun changeContentAndSave(content: String) {
        val text = content.trim()
        val post = edited.value
        if (post != null) {
            with(repository) {
                save(
                        post = post.copy(content = text),
                        object : PostRepository.GetAllCallback<Post> {
                            override fun onSuccess(data: Post) {
                                _postCreated.value = Unit
                            }

                            override fun onError(e: Exception) {
                                println(e.message)
                            }

                        })
            }
        }
        edited.value = empty
        loadPosts()
    }

    fun likeById(id: Long) {

        repository.likeById(id, object : PostRepository.GetAllCallback<Post> {
            override fun onSuccess(post: Post) {
                _data.value = _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .map {
                            if (it.id == id) {
                                post
                            } else {
                                it
                            }
                        }
                )
            }

            override fun onError(e: Exception) {
                print(e.message)
            }
        })
    }

    fun unlikeById(id: Long) {

        repository.unlikeById(id, object : PostRepository.GetAllCallback<Post> {
            override fun onSuccess(post: Post) {
                _data.value = _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .map {
                            if (it.id == id) {
                                post
                            } else {
                                it
                            }
                        }
                )
            }

            override fun onError(e: Exception) {
                print(e.message)
            }
        })
    }

    fun shareById(id: Long) {
        repository.shareById(id, object : PostRepository.GetAllCallback<Unit> {
            override fun onSuccess(data: Unit) {

            }

            override fun onError(e: Exception) {

            }
        })
    }

    fun cancelEdit() {
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }


}
