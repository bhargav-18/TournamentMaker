package com.example.tournamentmaker.authactivity.authfragments.ui.register

import android.app.Activity
import android.app.ProgressDialog
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
import com.example.tournamentmaker.databinding.FragmentRegisterUserBinding
import com.example.tournamentmaker.util.exhaustive
import com.example.tournamentmaker.util.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect


class RegisterFragment : Fragment(R.layout.fragment_register_user) {

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var binding: FragmentRegisterUserBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRegisterUserBinding.bind(view)
        binding.apply {

            etRegisterUname.setText(viewModel.username)
            etRegisterEmail.setText(viewModel.email)
            etRegisterPassword.setText(viewModel.password)
            etRegisterCpassword.setText(viewModel.repeatedPassword)

            etRegisterUname.addTextChangedListener {
                viewModel.username = it.toString()
            }
            etRegisterEmail.addTextChangedListener {
                viewModel.email = it.toString()
            }
            etRegisterPassword.addTextChangedListener {
                viewModel.password = it.toString()
            }
            etRegisterCpassword.addTextChangedListener {
                viewModel.repeatedPassword = it.toString()
            }

            tvLogin.setOnClickListener {
                findNavController().navigate(RegisterFragmentDirections.actionGlobalLoginFragment())
            }
            btnRegister.setOnClickListener {
                hideKeyboard(activity as Activity)
                showProgress(true)
                viewModel.register()
            }
            textViewRegisterToLogin.setOnClickListener {
                findNavController().navigate(RegisterFragmentDirections.actionGlobalLoginFragment())
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.registerEvent.collect { event ->
                @Suppress("IMPLICIT_CAST_TO_ANY")
                when (event) {
                    is RegisterViewModel.RegisterEvent.NavigateBackWithResult -> {
                        findNavController().navigate(RegisterFragmentDirections.actionGlobalLoginFragment())
                        showProgress(false)
                    }
                    is RegisterViewModel.RegisterEvent.ShowErrorMessage -> {
                        showProgress(false)
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }

    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressRegister.isVisible = bool
            if (bool) {
                parentLayoutRegister.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutRegister.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                etRegisterPassword.setText("")
                etRegisterCpassword.setText("")
            }
        }
    }
}