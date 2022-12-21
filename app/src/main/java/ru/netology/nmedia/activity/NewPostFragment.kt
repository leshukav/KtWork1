package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.AndroidUtils
import ru.netology.nmedia.StringArg
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.viewmodel.PostViewModel


class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg by StringArg
    }

    val viewModel by viewModels<PostViewModel>(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        val binding = FragmentNewPostBinding.inflate(inflater, container, false)

        arguments?.textArg?.let {
            binding.editPost.setText(it)
        }


        binding.editPost.requestFocus()
        binding.ok.setOnClickListener {
            val text = binding.editPost.text.toString()
            if (!text.isNullOrBlank()) {
                viewModel.changeContentAndSave(text)
            }
            AndroidUtils.hideKeyboard(requireView())
            viewModel.postCreated.observe(viewLifecycleOwner) {
                viewModel.loadPosts()
                findNavController().navigateUp()
            }
        }
        return binding.root
    }

}
