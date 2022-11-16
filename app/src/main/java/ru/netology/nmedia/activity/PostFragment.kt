package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.StringArg
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class PostFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel by viewModels<PostViewModel>(
            ownerProducer = ::requireParentFragment
        )
        val binding = FragmentPostBinding.inflate(inflater, container, false)
        arguments?.textArg?.let {
            binding.post.content.setText(it)
        }


        val viewHolder = PostViewHolder(binding.post, object : OnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
                viewModel.shareById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onEdit(post: Post) {
                 viewModel.edit(post)
            }

            override fun onPlay(post: Post) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(post.video)
                startActivity(intent)
            }

            override fun onPostFragment(post: Post) {

            }

        })

        viewModel.data.observe(viewLifecycleOwner) { posts ->

            val post = posts.find { it.content == arguments?.textArg } ?: run {
                findNavController().navigateUp()
                return@observe
            }

            viewHolder.bind(post)
        }

        viewModel.edited.observe(viewLifecycleOwner) {
            if (it.id == 0L) {
                return@observe
            }
            val text = it.content
            findNavController().navigate(R.id.action_postFragment2_to_newPostFragment,Bundle().apply {
                textArg = text
            } )
        }
        return binding.root
    }

}
