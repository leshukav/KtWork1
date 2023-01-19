package ru.netology.nmedia.repository

import ru.netology.nmedia.Post
import ru.netology.nmedia.api.PostsApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException

class PostRepositoryImpl: PostRepository {

    override fun getAllAsync(callback: PostRepository.GetAllCallback<List<Post>>) {

        PostsApi.retrofitService.getAll()
            .enqueue(object : Callback<List<Post>> {

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                   callback.onError(Exception(t))
                }

                override fun onResponse(
                    call: Call<List<Post>>,
                    response: Response<List<Post>>
                ) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }

                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }
            })
    }

    override fun removeById(id: Long, callback: PostRepository.GetAllCallback<Unit>) {
       PostsApi.retrofitService.removeById(id)
           .enqueue(object : Callback<Unit>{
               override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                 callback.onSuccess(Unit)
               }

               override fun onFailure(call: Call<Unit>, t: Throwable) {
                   callback.onError(Exception(t))
               }

           })

    }
    override fun save(post: Post, callback: PostRepository.GetAllCallback<Post>) {
        PostsApi.retrofitService.save(post)
            .enqueue(object : Callback<Post>{
                override fun onResponse(call: Call<Post>, response: Response<Post>) {

                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(Exception(t))
                }

            })
    }

    override fun likeById(id: Long, callback: PostRepository.GetAllCallback<Post>){
        PostsApi.retrofitService.likeById(id)
            .enqueue(object : Callback<Post>{
                override fun onResponse(
                    call: Call<Post>,
                    response: Response<Post>
                ) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))

                        return
                    }

                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(Exception(t))
                }

            })

    }

    override fun unlikeById(id: Long, callback: PostRepository.GetAllCallback<Post>) {
        PostsApi.retrofitService.unlikeById(id)
            .enqueue(object : Callback<Post>{
                override fun onResponse(
                    call: Call<Post>,
                    response: Response<Post>
                ) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }

                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(Exception(t))
                }

            })

    }

    override fun shareById(id: Long, callback: PostRepository.GetAllCallback<Unit>) {
        // TODO: do this in homework
    }


}