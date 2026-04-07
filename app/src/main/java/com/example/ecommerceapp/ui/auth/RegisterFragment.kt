package com.example.ecommerceapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.ecommerceapp.MainActivity
import com.example.ecommerceapp.R
import com.example.ecommerceapp.databinding.FragmentRegisterBinding
import com.example.ecommerceapp.utils.SessionManager

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)

        binding.btnRegister.setOnClickListener {
            val name = binding.etFullName.text.toString().trim()
            val email = binding.etRegisterEmail.text.toString().trim()
            val password = binding.etRegisterPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            when {
                name.isEmpty() -> showError("Enter full name")
                email.isEmpty() -> showError("Enter email address")
                password.isEmpty() -> showError("Enter password")
                confirmPassword.isEmpty() -> showError("Confirm your password")
                password != confirmPassword -> showError("Passwords do not match")
                else -> {
                    hideError()

                    val sessionManager = SessionManager(requireContext())
                    sessionManager.saveUser(name)

                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                }
            }
        }

        binding.tvGoToLogin.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showError(message: String) {
        binding.tvRegisterError.visibility = View.VISIBLE
        binding.tvRegisterError.text = message
    }

    private fun hideError() {
        binding.tvRegisterError.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}