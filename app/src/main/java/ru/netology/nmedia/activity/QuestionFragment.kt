package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentQuestionBinding


class QuestionFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentQuestionBinding.inflate(inflater, container, false)


        binding.buttonOk.setOnClickListener {
            AppAuth.getInstance().removeAuth()
            findNavController().navigate(R.id.action_questionFragment_to_feedFragment2)
        }

        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }
}