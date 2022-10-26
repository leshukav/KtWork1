package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import ru.netology.nmedia.databinding.ActivityMainBinding
import androidx.activity.viewModels
import androidx.core.view.isVisible
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = PostAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
                viewModel.shareById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }
        })
        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
        viewModel.edited.observe(this) {
            if (it.id != 0L) {
                binding.save.visibility = VISIBLE
                binding.group.visibility = VISIBLE
                binding.editContent.text = it.content
                binding.content.requestFocus()
                binding.content.setText(it.content)
            }
        }

        binding.content.setOnClickListener {
            binding.save.visibility = VISIBLE
        }
        binding.cancelButton.setOnClickListener {
            with(binding.content) {
                setText("")
                viewModel.cancelEdit()
                binding.save.visibility = INVISIBLE
                binding.group.visibility = INVISIBLE
                binding.group.visibility = View.GONE
                clearFocus()
                AndroidUtils.hideKeyboard(this)
            }
        }
        binding.content.setOnFocusChangeListener { _, hasFocus ->
            binding.save.isVisible = hasFocus
        }

        binding.save.setOnClickListener {
            with(binding.content) {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        "Content can't be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                viewModel.changeContent(text.toString())
                viewModel.seve()
                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
                binding.save.visibility = INVISIBLE
                binding.group.visibility = View.GONE
            }
        }

    }
}



