package ru.netology.nmedia.activity

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.activity.AppActivity.Companion.textArg
import ru.netology.nmedia.databinding.FragmentImageBinding

@AndroidEntryPoint
class ImageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentImageBinding.inflate(inflater, container, false)
        arguments?.textArg?.let {
            val uri: Uri = Uri.parse("http://10.0.2.2:9999/media/${it}")
            Glide.with(binding.imageFragment)
                .load(uri)
                .timeout(10_000)
                .into(binding.imageFragment)
        }
        return binding.root
    }

}
