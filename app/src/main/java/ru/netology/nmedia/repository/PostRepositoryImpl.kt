package ru.netology.nmedia.repository


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okio.IOException
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.api.PostsApi
import retrofit2.HttpException
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError


class PostRepositoryImpl(
    private val postDao: PostDao
) : PostRepository {
    override val data = postDao.getAllVisible().map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val response = PostsApi.retrofitService.getNewer(id)
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
            val response = PostsApi.retrofitService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val posts = response.body().orEmpty() //.map {
//                if (it.addServer) it else it.copy(addServer = true)
//            }
            //        println(posts)
            postDao.insert(posts.map(PostEntity::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun refresh() {
        try {
            val response = PostsApi.retrofitService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val posts = response.body().orEmpty()
            postDao.insertHidden(posts.map(PostEntity::fromDto) )
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

    override suspend fun getById(id: Long) {
        val postResponse = PostsApi.retrofitService.getById(id)
        if (!postResponse.isSuccessful) {
            throw HttpException(postResponse)
        }
//        val post = postResponse.body() ?: throw HttpException(postResponse)
//        postDao.insert(PostEntity.fromDto(post))
    }

    override suspend fun removeById(id: Long) {
        postDao.removeById(id)
        PostsApi.retrofitService.removeById(id)
    }

    override suspend fun saveOld(post: Post) {
        postDao.insert(PostEntity.fromDto(post))
    }

    override suspend fun save(post: Post) {
        postDao.insert(PostEntity.fromDto(post))
        val lastPost = postDao.getLastPost()
        try {
            val postResponse = PostsApi.retrofitService.save(post)
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
            val postResponse = PostsApi.retrofitService.likeById(id)
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
            val postResponse = PostsApi.retrofitService.unlikeById(id)
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

    override suspend fun shareById(id: Long) {
        // TODO: do this in homework
    }


}