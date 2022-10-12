package ru.netology.nmedia

data class Post(
    val id : Long,
    val author : String,
    val content : String,
    val publish : String,
    var like: Int = 0,
    var share: Int = 990,
    var likeById: Boolean = false
)
