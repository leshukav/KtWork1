package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import java.math.BigDecimal

class PostRepositoryInMemoryImpl : PostRepository {
    private var post = Post(
        id = 1,
        author = "Нетология. Университет интернет-профессий будущего",
        content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        publish = "21 мая в 18:36",
        likeByMy = false
    )
    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> = data
    override fun like() {
        if (!post.likeByMy) {
            post = post.copy(like = post.like + 1, likeByMy = !post.likeByMy)
            R.drawable.ic_liked_24
        } else {
            post = post.copy(like = post.like - 1, likeByMy = !post.likeByMy)
            R.drawable.ic_like_24
        }
        data.value = post
    }

    override fun share() {
        post = post.copy(share = post.share + 10)
        data.value = post
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
