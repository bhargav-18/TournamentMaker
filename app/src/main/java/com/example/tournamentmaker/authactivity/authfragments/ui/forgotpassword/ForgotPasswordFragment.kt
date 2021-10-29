package com.example.tournamentmaker.authactivity.authfragments.ui.forgotpassword

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tournamentmaker.R
import com.example.tournamentmaker.authactivity.authfragments.ui.login.LoginViewModel
import com.example.tournamentmaker.databinding.FragmentForgotPasswordBinding
import com.example.tournamentmaker.databinding.FragmentLoginUserBinding
import com.example.tournamentmaker.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {

    private val viewModel: ForgotPasswordViewModel by viewModels()
    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentForgotPasswordBinding.bind(view)
        binding.apply {
            editTextEmailForgot.setText(viewModel.email)

            editTextEmailForgot.addTextChangedListener {
                viewModel.email = it.toString()
            }

            buttonForgotPassword.setOnClickListener {
                viewModel.forgotPassword()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.forgotPasswordEvent.collect { event ->
                @Suppress("IMPLICIT_CAST_TO_ANY")
                when (event) {
                    is ForgotPasswordViewModel.ForgotPasswordEvent.NavigateBackWithResult -> {
                        Snackbar.make(
                            requireView(),
                            "Password reset link sent to your email",
                            Snackbar.LENGTH_LONG
                        ).show()
                        findNavController().navigate(ForgotPasswordFragmentDirections.actionGlobalLoginFragment())
                    }
                    is ForgotPasswordViewModel.ForgotPasswordEvent.ShowErrorMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }
    }
}