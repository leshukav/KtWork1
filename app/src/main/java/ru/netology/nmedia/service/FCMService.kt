package ru.netology.nmedia.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Recipient
import ru.netology.nmedia.repository.PostRepositoryImpl
import java.util.jar.Manifest
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
        val currentId = AppAuth.getInstance().data.value?.id
        val body = gson.fromJson(message.data[content], Recipient::class.java)
        val recipient = body.recipientId

        when (recipient) {
            currentId -> handleOk()
            null ->  handle()

        }
        if (recipient == 0L && currentId != body.recipientId) {
            AppAuth.getInstance().sendPushToken()
        }
        if (recipient != 0L && currentId != body.recipientId && recipient != null) {
            AppAuth.getInstance().sendPushToken()
        }
    }

    private fun handle() {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Mass mailing")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun handleOk() {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Authorization Ok")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    override fun onNewToken(token: String) {
        AppAuth.getInstance().sendPushToken(token)
        println(token)
    }

//    private fun handlePost() {
//       val dao = PostRepositoryImpl(AppDb.getInstance(context = application).postDao())
//       val post = dao.getLastPost()
//
//        val notification = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.ic_notification)
//            .setContentTitle(getString(R.string.new_post, post.author))
//            .setLargeIcon(
//                BitmapFactory.decodeResource(
//                    resources,
//                    ic_launcher
//                )
//            )
//            .setStyle(
//                NotificationCompat.BigTextStyle()
//                    .bigText(
//                        getString(
//                            R.string.notification_new_post,
//                            post.content
//                        )
//                    )
//            )
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .build()
//
//        NotificationManagerCompat.from(this)
//            .notify(Random.nextInt(100_000), notification) }


//
//    private fun handleLike(content: Like) {
//        val notification = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.ic_notification)
//            .setContentTitle(
//                getString(
//                    R.string.notification_user_liked,
//                    content.userName,
//                    content.postAuthor,
//                )
//            )
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .build()
//
//        NotificationManagerCompat.from(this)
//            .notify(Random.nextInt(100_000), notification)
//    }
}

//enum class Action {
//    LIKE, NEWPOST
//}

//
//data class Like(
//    val userId: Long,
//    val userName: String,
//    val postId: Long,
//    val postAuthor: String,
//)