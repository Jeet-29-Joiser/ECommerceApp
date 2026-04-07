package com.example.ecommerceapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.ecommerceapp.MainActivity
import com.example.ecommerceapp.R
import com.example.ecommerceapp.databinding.FragmentLoginBinding
import com.example.ecommerceapp.utils.SessionManager

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            when {
                email.isEmpty() -> showError("Enter email address")
                password.isEmpty() -> showError("Enter password")
                else -> {
                    hideError()

                    val userName = email.substringBefore("@")
                        .replaceFirstChar { it.uppercase() }

                    val sessionManager = SessionManager(requireContext())
                    sessionManager.saveUser(userName)

                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                }
            }
        }

        binding.tvGoToRegister.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.auth_nav_host, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun showError(message: String) {
        binding.tvLoginError.visibility = View.VISIBLE
        binding.tvLoginError.text = message
    }

    private fun hideError() {
        binding.tvLoginError.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}