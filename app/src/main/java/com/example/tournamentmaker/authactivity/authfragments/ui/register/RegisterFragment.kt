package com.example.tournamentmaker.authactivity.authfragments.ui.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tournamentmaker.R
import com.example.tournamentmaker.databinding.FragmentRegisterUserBinding
import com.example.tournamentmaker.mainactivity.MainActivity
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
                viewModel.register()
                etRegisterPassword.setText("")
                etRegisterCpassword.setText("")
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
                    }
                    is RegisterViewModel.RegisterEvent.ShowErrorMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }

    }

}