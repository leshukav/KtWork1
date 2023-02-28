package ru.netology.nmedia.model

data class AuthModel (
    val id: Long = 0,
    val token: String? = null,
)

data class AuthModelState(
    val authorized: Boolean = false,
    val errorCode: Boolean = false
        )
