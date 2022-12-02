package ru.netology.nmedia.service

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.R.mipmap.ic_launcher
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.PostRepositoryImpl
import kotlin.random.Random


class FCMService() : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val checkEnum: Boolean =
            try {
                message.data[action]?.let { Action.valueOf(it) }
                true
            } catch (e: Exception) {
                false
            }

        if (checkEnum) {
            message.data[action]?.let {
                when (Action.valueOf(it)) {
                    Action.LIKE -> handleLike(
                        gson.fromJson(
                            message.data[content],
                            Like::class.java
                        )
                    )
                    Action.NEWPOST -> handlePost()
                }
            }
        }
//        } else {   Пока не знаю можно ли кинуть тост...
//            Toast.makeText(
//                this@FCMService,
//                "Action is wrong",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
    }

    override fun onNewToken(token: String) {
        println(token)
    }

    private fun handlePost() {
        val dao = PostRepositoryImpl(AppDb.getInstance(context = application).postDao())
        val post = dao.getLastPost()

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.new_post, post.author))
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    ic_launcher
                )
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        getString(
                            R.string.notification_new_post,
                            post.content
                        )
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }
}

enum class Action {
    LIKE, NEWPOST
}

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)