package ru.netology.nmedia.auth

import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import retrofit2.http.Url
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.Recipient
import ru.netology.nmedia.model.AuthModel
import java.net.HttpCookie.parse

class AppAuth private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    private val _data: MutableStateFlow<AuthModel?>

    init {
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getLong(ID_KEY, 0L)
        if (token == null || id == 0L) {
            _data = MutableStateFlow(null)
            prefs.edit { clear() }
        } else {
            _data = MutableStateFlow(AuthModel(id, token))
        }
        sendPushToken()
    }

    val data: StateFlow<AuthModel> = _data.asStateFlow() as StateFlow<AuthModel>

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _data.value = AuthModel(id, token)
        prefs.edit {
            putLong(ID_KEY, id)
            putString(TOKEN_KEY, token)
        }
        sendPushToken()
    }

    @Synchronized
    fun removeAuth() {
        _data.value = null
        prefs.edit { clear() }
        sendPushToken()
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val pushToken = token ?: Firebase.messaging.token.await()
                val response = PostsApi.retrofitService.sendPushToken(PushToken(pushToken))
                if (response.isSuccessful) {
                   val res = PostsApi.retrofitService.checkRecipientId(pushToken, Recipient(555,"Wow!"))
                    println(res.body())
               }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppAuth? = null
        private const val TOKEN_KEY = "TOKEN_KEY"
        private const val ID_KEY = "ID_KEY"
        fun getInstance(): AppAuth = synchronized(this) {
            requireNotNull(INSTANCE) {
                "You must call init(context: Context)"
            }
        }

        fun init(context: Context): AppAuth = synchronized(this) {
            INSTANCE ?: AppAuth(context).apply {
                INSTANCE = this
            }
        }

    }

}
