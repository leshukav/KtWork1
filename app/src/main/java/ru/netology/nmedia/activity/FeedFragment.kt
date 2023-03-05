package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.ImageFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {

    lateinit var adapter: PostAdapter

    private val viewModel: PostViewModel by activityViewModels()

    private val authViewModel: AuthViewModel by activityViewModels()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        adapter = PostAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                if (!post.likedByMe) {
                    viewModel.likeById(post.id)
                } else {
                    viewModel.unlikeById(post.id)
                }
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
                if (post.id == 0L) {
                    return
                }
                val text = post.content   //content
                findNavController().navigate(
                    R.id.action_feedFragment2_to_newPostFragment,
                    Bundle().apply {
                        textArg = text
                    })
                viewModel.edit(post)
            }

            override fun onImage(post: Post) {
                val url = post.attachment?.url.toString()
                findNavController().navigate(R.id.action_feedFragment2_to_imageFragment,
                    Bundle().apply {
                        textArg = url
                    })

//                val intent = Intent(Intent.ACTION_VIEW)
//                intent.data = Uri.parse(post.video)
//                startActivity(intent)
            }

        })

        binding.list.adapter = adapter
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading

            if (state.error) {
                Snackbar.make(binding.root, R.string.error, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {
                        viewModel.loadPosts()
                    }
                    .show()
                binding.errorGroup.isVisible = false
            }
            if (state.likeError) {
                Snackbar.make(binding.root, "You must register", Snackbar.LENGTH_LONG)
                    .setAction("Ok") {
                        findNavController().navigate(R.id.action_feedFragment2_to_dialogFragment,
                            Bundle().apply
                            { textArg = "loginGroup" })
                    }
                    .show()
            }
            if (state.removeError) {
                Snackbar.make(binding.root, "Failed to connect. Try later", Snackbar.LENGTH_LONG)
                    .setAction("Ok") {}
                    .show()
            }

            binding.errorGroup.isVisible = false
            binding.swiperefresh.isRefreshing = state.refreshing

        }

//        viewModel.newerCount.observe(viewLifecycleOwner) { count ->
//            if (count > 0) {
//                binding.chip.isVisible = true
//                binding.chip.text = "You have new $count posts"
//            }
//            println("Newer couunt $count")
//        }

        authViewModel.data.observe(viewLifecycleOwner) {
            loadLatestPosts()
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                binding.swiperefresh.isRefreshing = it.refresh is LoadState.Loading
                        || it.append is LoadState.Loading
                        || it.prepend is LoadState.Loading
            }
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        })

        binding.chip.setOnClickListener {
            binding.chip.isVisible = false
            viewModel.loadNewer()
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }
        binding.swiperefresh.setOnRefreshListener {
            adapter.refresh()
            // viewModel.refreshPosts()
        }

        binding.fab.setOnClickListener {
            if (authViewModel.authorized) {
                findNavController().navigate(R.id.action_feedFragment2_to_newPostFragment)
            } else {
                findNavController().navigate(R.id.action_feedFragment2_to_dialogFragment,
                    Bundle().apply
                    { textArg = "loginGroup" })
            }
        }
        return binding.root
    }

    private fun loadLatestPosts() {
        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }
    }
}




