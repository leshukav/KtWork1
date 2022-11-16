package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel by viewModels<PostViewModel>(
            ownerProducer = ::requireParentFragment
        )
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostAdapter(object : OnInteractionListener {
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
                findNavController().navigate(R.id.action_feedFragment2_to_postFragment2,
                    Bundle().apply
                    {
                        textArg = post.content
                    })
            }
        })
        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }


        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment2_to_newPostFragment)
        }

        viewModel.edited.observe(viewLifecycleOwner) {
            if (it.id == 0L) {
                return@observe
            }
            val text = it.content
            findNavController().navigate(
                R.id.action_feedFragment2_to_newPostFragment,
                Bundle().apply {
                    textArg = text
                })
        }
        return binding.root
    }
}



