package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.databinding.ActivityMainBinding
import androidx.activity.viewModels
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.data.observe(this) { post ->
            binding.apply {
                author.text = post.author
                publish.text = post.publish
                content.text = post.content
                likeCount.text = DisplayCount.logic(post.like)
                shareCount.text = DisplayCount.logic(post.share)
                like.setImageResource(
                    if (post.likeByMy) R.drawable.ic_liked_24 else R.drawable.ic_like_24
                )
            }
        }
        binding.like.setOnClickListener {
            viewModel.like()
        }
        binding.share.setOnClickListener {
            viewModel.share()
        }


    }

}


