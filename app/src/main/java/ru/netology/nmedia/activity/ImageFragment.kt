package ru.netology.nmedia.activity

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.StringArg
import ru.netology.nmedia.databinding.FragmentImageBinding
import ru.netology.nmedia.viewmodel.PostViewModel


class ImageFragment: Fragment() {

    companion object {
        var Bundle.textArg by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel by viewModels<PostViewModel>(
            ownerProducer = ::requireParentFragment
        )
        val binding = FragmentImageBinding.inflate(inflater, container, false)
        arguments?.textArg?.let {
            val uri: Uri = Uri.parse("http://10.0.2.2:9999/media/${it}")
           binding.imageFragment.setImageURI(uri)
        }

        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val imageFragment: ImageView = view.findViewById(R.id.imageFragment)
//        arguments?.textArg?.let {
//            val uri: Uri = Uri.parse("http://10.0.2.2:9999/media/${it}")
//            imageFragment.setImageURI(uri)
//        }
//    }
}
