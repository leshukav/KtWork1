package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.AndroidUtils
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.AppActivity.Companion.LOGIN_GROUP
import ru.netology.nmedia.activity.AppActivity.Companion.QUESTION_GROUP
import ru.netology.nmedia.activity.AppActivity.Companion.REGISTR_GROUP
import ru.netology.nmedia.activity.AppActivity.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentLoginBinding
import ru.netology.nmedia.viewmodel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : DialogFragment() {

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val authViewModel: AuthViewModel by activityViewModels()

        val binding = FragmentLoginBinding.inflate(inflater, container, false)
        arguments?.textArg?.let {
            when (it) {
                LOGIN_GROUP -> {
                    binding.loginGroup.isVisible = true
                    true
                }
                REGISTR_GROUP -> {
                    binding.registrGroup.isVisible = true
                    true
                }
                QUESTION_GROUP -> {
                    binding.questionGroup.isVisible = true
                    true
                }
                else -> false
            }

        }
        binding.buttonLogin.setOnClickListener {
            val login = binding.login.text.toString()
            if (!login.isNullOrBlank()) {
                val pass = binding.password.text.toString()
                AndroidUtils.hideKeyboard(requireView())
                binding.buttonLogin.let { button ->
                    button.isClickable = false
                    button.text = getString(R.string.Wait_authorization)
                }
                binding.loading.isVisible = true
                binding.login.isEnabled = false
                binding.password.isEnabled = false

                authViewModel.authorization(login, pass)
            } else {
                Toast.makeText(context, R.string.Login_is_Null, Toast.LENGTH_LONG).show()
            }
        }

        authViewModel.data.observe(viewLifecycleOwner) {
            if (authViewModel.authorized && !binding.questionGroup.isVisible) {
                findNavController().navigate(R.id.feedFragment2)

            }
        }

        authViewModel.state.observe(viewLifecycleOwner) { state ->
            if (state.errorCode) {
                with(binding) {
                    buttonLogin.text = "Login"
                    buttonLogin.isClickable = true
                    loading.isVisible = false
                    login.isEnabled = true
                    password.isEnabled = true
                    login.setText("")
                    password.setText("")
                }
                Toast.makeText(context, R.string.Login_not_found, Toast.LENGTH_LONG).show()
            }
        }
        binding.registrButton.setOnClickListener {
            val name = binding.name.text.toString()
            val login = binding.loginRegistr.text.toString()
            if (!login.isNullOrBlank() || !name.isNullOrBlank()) {
                val pass = binding.passRegistr.text.toString()
                val passCheck = binding.passCheckRegistr.text.toString()
                if (pass.equals(passCheck)) {
                    AndroidUtils.hideKeyboard(requireView())
                    with(binding) {
                        registrButton.let { button ->
                            button.isClickable = false
                            button.text = "Wait authorization"
                        }
                        loading.isVisible = true
                        loginRegistr.isEnabled = false
                        passRegistr.isEnabled = false
                        passCheckRegistr.isEnabled = false
                    }

                    authViewModel.registration(name, login, pass)
                } else {
                    Toast.makeText(context, R.string.Password_not_correct, Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, R.string.Login_Name_is_not_empty, Toast.LENGTH_LONG).show()

            }
        }
        binding.buttonOk.setOnClickListener {
            appAuth.removeAuth()
            if (binding.questionGroup.isVisible) {
                findNavController().navigateUp()
            }
        }

        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }


}