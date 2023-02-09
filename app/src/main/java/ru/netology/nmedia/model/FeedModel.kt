package ru.netology.nmedia.model

import ru.netology.nmedia.dto.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = posts.isEmpty(),
)

data class FeedModelState(
    val loading: Boolean = false,
    val loadError: Boolean = loading,
    val error: Boolean = false,
    val refreshing: Boolean = false,
    val likeError: Boolean = false,
    val removeError: Boolean = false,
    val addServer: Boolean = true,
)