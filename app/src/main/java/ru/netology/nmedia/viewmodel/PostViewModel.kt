package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import androidx.lifecycle.switchMap
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.MediaModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject

private val empty = Post(
    id = 0,
    authorId = 0L,
    content = "",
    author = "Pushkin",
    likedByMe = false,
    published = 26122022,
)
@HiltViewModel
@ExperimentalCoroutinesApi
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    appAuth: AppAuth,
) : ViewModel() {

    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state

    val data: Flow<PagingData<Post>> = appAuth
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.data
                .map { posts ->
                       posts.map {it.copy(ownedByMe = it.authorId == myId)}
                }
        }.flowOn(Dispatchers.Default)


//    val newerCount: LiveData<Int> = data.switchMap {
//        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
//            .catch { e -> e.printStackTrace() }
//            .asLiveData(Dispatchers.Default)
//    }

    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _media = MutableLiveData<MediaModel?>(null)
    val media: MutableLiveData<MediaModel?>
        get() = _media

    init {
        loadPosts()
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _media.value = MediaModel(uri, file)
    }

    fun clearPhoto() {
        _media.value = null
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

    suspend fun unreadCount(): Int {
        return repository.unreadCount()
    }

    fun refreshPosts() {
        _state.value = FeedModelState(refreshing = true)
        viewModelScope.launch {
            try {
                repository.refresh()     //getAll()
                _state.value = FeedModelState(refreshing = false)
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    fun removeById(id: Long) {
   //     val postOld: Post? = data.value?.posts?.find { it.id == id }
        viewModelScope.launch {
            try {
                repository.removeById(id)
                _state.value = FeedModelState(removeError = false)
            } catch (e: Exception) {
                _state.value = FeedModelState(removeError = true)
  //              postOld?.let { repository.saveOld(it) }
            }
        }
    }


    fun changeContentAndSave(content: String) {
        val text = content.trim()
        val post = edited.value
        if (post != null) {
            viewModelScope.launch {
                try {
                    when (val media = media.value) {
                        null -> repository.save(post = post.copy(content = text))
                        else -> {
                            repository.saveWithAttachment(post = post.copy(content = text), media)
                        }
                    }
                    _postCreated.value = Unit
                    edited.value = empty
                    clearPhoto()
                    _state.value = FeedModelState()
                    //      repository.save(post = post.copy(content = text))
                } catch (e: Exception) {
                    _state.value = FeedModelState(error = true)
                }
            }
        }

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