package ru.netology.nmedia.model

data class AuthModel (
    val id: Long,
    val token: String
)

data class AuthModelState(
    val authorized: Boolean = false,
    val errorCode: Boolean = false
        )
