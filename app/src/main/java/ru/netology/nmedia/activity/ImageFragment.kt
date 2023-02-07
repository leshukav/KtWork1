package ru.netology.nmedia.activity

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.netology.nmedia.R
import ru.netology.nmedia.StringArg
import ru.netology.nmedia.databinding.FragmentImageBinding
import ru.netology.nmedia.viewmodel.PostViewModel
import java.net.URL

class ImageFragment: Fragment() {

    companion object {
        var Bundle.textArg by StringArg
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val viewModel by viewModels<PostViewModel>(
//            ownerProducer = ::requireParentFragment
//        )
        val binding = FragmentImageBinding.inflate(inflater, container, false)
        arguments?.textArg?.let {
            val uri: Uri = Uri.parse("http://10.0.2.2:9999/media/${it}")
            binding.imageFragment.setImageURI(uri)
            binding.imageFragment.setImageResource(R.drawable.ic_error_100)
            binding.text.text = it
        }
        return binding.root
    }

}
