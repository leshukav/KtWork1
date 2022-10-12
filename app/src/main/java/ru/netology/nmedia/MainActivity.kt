package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.databinding.ActivityMainBinding
import java.math.BigDecimal


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            publish = "11 октября в 15:19",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb"
        )
        binding.apply {
            author.text = post.author
            publish.text = post.publish
            content.text = post.content
            likeCount.text = logic(post.like)
            textShare.text = logic(post.share)
            viewCount.text = "200"
            if (post.likeById) like.setImageResource(R.drawable.ic_liked_24)

            like.setOnClickListener {
                post.likeById = !post.likeById
                if (post.likeById) {
                    like.setImageResource(R.drawable.ic_liked_24)
                    post.like++
                    likeCount.text = logic(post.like)
                } else {
                    like.setImageResource(R.drawable.ic_like_24)
                    post.like--
                    likeCount.text = logic(post.like)
                }
            }
            share.setOnClickListener {
                post.share += 10
                textShare.text = logic(post.share)
            }


        }

    }

    fun logic(count: Int): String {
        return when (count) {
            in 1000..9999 -> {
                ((count / 1000.0).toBigDecimal().setScale(1, BigDecimal.ROUND_DOWN)
                    .toString() + "K")
            }
            in 10_000..999_999 -> {
                ((count / 1000.0).toBigDecimal().setScale(0, BigDecimal.ROUND_DOWN)
                    .toString() + "K")
            }
            in 1_000_000..10_000_000 -> {
                (count / 1_000_000.0).toBigDecimal().setScale(0, BigDecimal.ROUND_DOWN)
                    .toString() + "M"
            }
            else -> {
                count.toString()
            }
        }
    }
}