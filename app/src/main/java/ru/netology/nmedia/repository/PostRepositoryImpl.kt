package ru.netology.nmedia.repository


import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dao.PostDao
import retrofit2.HttpException
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.model.MediaModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: PostsApiService,
    private val appAuth: AppAuth,
) : PostRepository {
    override val data = Pager(
       config = PagingConfig(10, enablePlaceholders = false),
       pagingSourceFactory = {
           PostPagingSource(
               apiService
           )
       }
    ).flow
        //postDao.getAllVisible()
//        .map(List<PostEntity>::toDto)
//        .flowOn(Dispatchers.Default)

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val response = apiService.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity().map {
                it.copy(hidden = true)
            })
            emit(body.size)
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {

            val response = apiService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val posts = response.body().orEmpty()
            postDao.insert(posts.map(PostEntity::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun refresh() {
        try {
            val response = apiService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val posts = response.body().orEmpty()
            postDao.insertHidden(posts.map(PostEntity::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun unreadCount(): Int {
        return postDao.getUnreadCount()
    }

    override suspend fun loadNewer() {
        postDao.readAll()
    }

    override suspend fun saveWithAttachment(post: Post, mediaModel: MediaModel) {
        try {
            val media = upload(mediaModel)
            val response = apiService.save(
                post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun upload(upload: MediaModel): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file?.name, upload.file?.asRequestBody() ?: throw UnknownError
            )

            val response = apiService.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getById(id: Long) {
        val postResponse = apiService.getById(id)
        if (!postResponse.isSuccessful) {
            throw HttpException(postResponse)
        }
//        val post = postResponse.body() ?: throw HttpException(postResponse)
//        postDao.insert(PostEntity.fromDto(post))
    }

    override suspend fun removeById(id: Long) {
        postDao.removeById(id)
        apiService.removeById(id)

    }

    override suspend fun saveOld(post: Post) {
        postDao.insert(PostEntity.fromDto(post))
    }

    override suspend fun save(post: Post) {
        postDao.insert(PostEntity.fromDto(post))
        val lastPost = postDao.getLastPost()
        try {
            val postResponse = apiService.save(post)
            if (!postResponse.isSuccessful) {
                throw ApiError(postResponse.code(), postResponse.message())
            }
            val body = postResponse.body() ?: throw HttpException(postResponse)
            postDao.removeById(lastPost.id)
            postDao.insert(PostEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    }

    override suspend fun likeById(id: Long) {
        postDao.likeById(id)

        try {
            val postResponse = apiService.likeById(id)
            if (!postResponse.isSuccessful) {
                throw ApiError(postResponse.code(), postResponse.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun unlikeById(id: Long) {
        postDao.likeById(id)
        try {
            val postResponse = apiService.unlikeById(id)
            if (!postResponse.isSuccessful) {
                throw ApiError(postResponse.code(), postResponse.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun cancelLike(id: Long) {
        postDao.likeById(id)
    }

    override suspend fun authorization(login: String, pass: String) {
        try {
            val response = apiService.updateUser(login, pass)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val field = response.body() ?: throw HttpException(response)
            field.token?.let { appAuth.setAuth(field.id, it) }
        } catch (e: IOException) {
            throw NetworkError
        }

    }

    override suspend fun registration(name: String, login: String, pass: String) {
        try {
            val response = apiService.registerUser(login, pass, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val field = response.body() ?: throw HttpException(response)
            field.token?.let { appAuth.setAuth(field.id, it) }
        } catch (e: IOException) {
            throw NetworkError
        }
    }


    override suspend fun shareById(id: Long) {
        // TODO: do this in homework
    }


}