package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
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
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(application).postDao()
    )
    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state

    val data: LiveData<FeedModel> = repository.data
        .map(::FeedModel)
        .asLiveData(Dispatchers.Default)

    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)
    }

    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    suspend fun unreadCount(): Int {
    return repository.unreadCount()
    }

    fun loadPosts() {
        _state.value = FeedModelState(loading = true)
        viewModelScope.launch {
            try {
                repository.getAll()
                _state.value = FeedModelState()
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    fun loadNewer() {
        viewModelScope.launch {
            try {
                repository.loadNewer()
                _state.value = FeedModelState()
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    fun refreshPosts() {
        _state.value = FeedModelState(refreshing = true)
        viewModelScope.launch {
            try {
                repository.refresh()                //getAll()
                _state.value = FeedModelState(refreshing = false)
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    fun removeById(id: Long) {
        val postOld: Post? = data.value?.posts?.find { it.id == id }
        viewModelScope.launch {
            try {
                repository.removeById(id)
                _state.value = FeedModelState(removeError = false)
            } catch (e: Exception) {
                _state.value = FeedModelState(removeError = true)
                postOld?.let { repository.saveOld(it) }
            }
        }
    }


    fun changeContentAndSave(content: String) {
        val text = content.trim()
        val post = edited.value
        if (post != null) {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    repository.save(post = post.copy(content = text))
                } catch (e: Exception) {
                    _state.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.likeById(id)
                _state.value = FeedModelState(likeError = false)
            } catch (e: Exception) {
                repository.cancelLike(id)
                _state.value = FeedModelState(likeError = true)

            }
        }
    }

    fun unlikeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.unlikeById(id)
                _state.value = FeedModelState(likeError = false)
            } catch (e: Exception) {
                repository.cancelLike(id)
                _state.value = FeedModelState(likeError = true)

            }
        }
    }

    fun shareById(id: Long) {
        viewModelScope.launch {
            try {
                repository.shareById(id)
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    fun cancelEdit() {
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }


}
