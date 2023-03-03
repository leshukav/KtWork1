package ru.netology.nmedia.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.dto.Post
import java.io.IOException

class PostPagingSource(
    private val apiService: PostsApiService
): PagingSource<Long, Post>() {
    override fun getRefreshKey(state: PagingState<Long, Post>): Long? = null

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        try {
            val result = when (params) {
                is LoadParams.Refresh -> {
                    apiService.getLatest(params.loadSize)
                }
                is LoadParams.Append -> {
                    apiService.getAfter(id = params.key, count = params.loadSize)
                }
                is LoadParams.Prepend -> return LoadResult.Page(
                    data = emptyList(), nextKey = null, prevKey = params.key
                )
            }
            if (!result.isSuccessful) {
                throw HttpException(result)
            }
            val date = result.body().orEmpty()
            return LoadResult.Page(
                data = date,
                prevKey = params.key,
                nextKey = date.lastOrNull()?.id
            )
        } catch (e: IOException) {
           return LoadResult.Error(e)
        }
    }
}