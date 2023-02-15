package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.AndroidUtils
import ru.netology.nmedia.activity.ImageFragment.Companion.textArg
import ru.netology.nmedia.databinding.FragmentLoginBinding
import ru.netology.nmedia.viewmodel.AuthViewModel


class LoginFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val authViewModel by viewModels<AuthViewModel>()

        val binding = FragmentLoginBinding.inflate(inflater, container, false)
        arguments?.textArg?.let {
            if (it.equals("loginGroup")) {
                binding.loginGroup.isVisible = true
            } else if (it.equals("registrGroup")) {
                binding.registrGroup.isVisible = true
            }

        }
        binding.buttonLogin.setOnClickListener {
            val login = binding.login.text.toString()
            if (!login.isNullOrBlank()) {
                val pass = binding.password.text.toString()
                AndroidUtils.hideKeyboard(requireView())
                binding.buttonLogin.let { button ->
                    button.isClickable = false
                    button.text = "Wait authorization"
                }
                binding.loading.isVisible = true
                binding.login.isEnabled = false
                binding.password.isEnabled = false

                authViewModel.authorization(login, pass)
            } else {
                Toast.makeText(context, "Login is Null", Toast.LENGTH_LONG).show()
            }
        }

        authViewModel.data.observe(viewLifecycleOwner) {
            if (authViewModel.authorized) {
                findNavController().navigateUp()
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
                Toast.makeText(context, "Login not found", Toast.LENGTH_LONG).show()
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
                    Toast.makeText(context, "Password not correct", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Login/Name is not empty", Toast.LENGTH_LONG).show()

            }
        }
        return binding.root
    }


}