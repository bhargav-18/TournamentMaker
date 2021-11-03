package com.example.tournamentmaker.authactivity.authfragments.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tournamentmaker.R
import com.example.tournamentmaker.databinding.FragmentLoginUserBinding
import com.example.tournamentmaker.mainactivity.MainActivity
import com.example.tournamentmaker.util.exhaustive
import com.example.tournamentmaker.util.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class LoginFragment : Fragment(R.layout.fragment_login_user) {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginUserBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginUserBinding.bind(view)
        binding.apply {

            etLoginEmail.setText(viewModel.email)
            etLoginPassword.setText(viewModel.password)

            etLoginEmail.addTextChangedListener {
                viewModel.email = it.toString()
            }
            etLoginPassword.addTextChangedListener {
                viewModel.password = it.toString()
            }

            tvSignUp.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionGlobalRegisterFragment())
            }
            btnLogin.setOnClickListener {
                hideKeyboard(activity as Activity)
                showProgress(true)
                viewModel.login()
                etLoginPassword.setText("")
            }
            tvForgotPassword.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
            }
            textViewLoginToRegister.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionGlobalRegisterFragment())
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.loginEvent.collect { event ->
                @Suppress("IMPLICIT_CAST_TO_ANY")
                when (event) {
                    is LoginViewModel.LoginEvent.NavigateBackWithResult -> {
                        showProgress(false)
                        Intent(requireContext(), MainActivity::class.java).also {
                            startActivity(it)
                            requireActivity().finish()
                        }
                    }
                    is LoginViewModel.LoginEvent.ShowErrorMessage -> {
                        showProgress(false)
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }
    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressLogin.isVisible = bool
            if (bool) {
                parentLayoutLogin.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutLogin.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                etLoginPassword.setText("")
            }
        }
    }

}