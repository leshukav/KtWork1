package ru.netology.nmedia.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nmedia.AndroidUtils
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.ImageFragment.Companion.textArg
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class NewPostFragment : Fragment() {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val viewModel: PostViewModel by activityViewModels()

    private val authViewModel: AuthViewModel by activityViewModels()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentNewPostBinding.inflate(inflater, container, false)

        arguments?.textArg?.let {
            binding.edit.setText(it)
        }
        viewModel.edited.observe(viewLifecycleOwner) { post ->
            //    binding.edit.setText(post.content)
            binding.photoContainer.isVisible = true
            val url = post.attachment?.url
            val uri: Uri = Uri.parse("http://10.0.2.2:9999/media/$url")
            Glide.with(binding.preview)
                .load(uri)
                .timeout(10_000)
                .into(binding.preview)
            binding.clear.isVisible = true
        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        viewModel.changePhoto(uri, uri?.toFile())
                    }
                }
            }

        binding.gallary.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.photo.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickPhotoLauncher::launch)
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_new_post, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.new_post -> {
                        val text = binding.edit.text.toString()
                        if (text.isNotBlank()) {
                            this@NewPostFragment.viewModel.changeContentAndSave(text)
                        }
                        AndroidUtils.hideKeyboard(requireView())
                        viewModel.loadNewer()
                        findNavController().navigateUp()
                        true
                    }
                    else -> false
                }

        }, viewLifecycleOwner)

        binding.clear.setOnClickListener {
            viewModel.clearPhoto()
        }

        authViewModel.data.observe(viewLifecycleOwner) {
            if (!authViewModel.authorized) {
                findNavController().navigateUp()
            }
        }

        viewModel.media.observe(viewLifecycleOwner) { media ->
            if (media == null) {
                binding.photoContainer.isGone = true
                return@observe
            }
            binding.photoContainer.isVisible = true
            binding.preview.setImageURI(media.uri)
        }

        binding.edit.requestFocus()

        return binding.root
    }

}
