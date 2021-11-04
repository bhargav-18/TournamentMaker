package com.example.tournamentmaker.authactivity.authfragments.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tournamentmaker.R
import com.example.tournamentmaker.databinding.FragmentAuthScreenBinding
import com.example.tournamentmaker.mainactivity.MainActivity
import com.example.tournamentmaker.util.exhaustive
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class AuthFragment : Fragment(R.layout.fragment_auth_screen) {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentAuthScreenBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAuthScreenBinding.bind(view)
        binding.apply {
            buttonNavigateToRegisterScreen.setOnClickListener {
                findNavController().navigate(AuthFragmentDirections.actionGlobalRegisterFragment())
            }
            textViewAlreadyUserLogin.setOnClickListener {
                findNavController().navigate(AuthFragmentDirections.actionGlobalLoginFragment())
            }
            buttonGoogleSignIn.setOnClickListener {
                val option = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .requestProfile()
                    .build()
                val signInClient = GoogleSignIn.getClient(requireActivity(), option)
                signInClient.signInIntent.also {
                    startActivityForResult(it, REQUEST_CODE_SIGN_IN)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.authEvent.collect { event ->
                @Suppress("IMPLICIT_CAST_TO_ANY")
                when (event) {
                    is AuthViewModel.AuthEvent.NavigateBackWithResult -> {
                        showProgress(false)
                        Intent(requireContext(), MainActivity::class.java).also {
                            startActivity(it)
                            requireActivity().finish()
                        }
                    }
                    is AuthViewModel.AuthEvent.ShowErrorMessage -> {
                        showProgress(false)
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }

    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressAuth.isVisible = bool
            if (bool) {
                parentLayoutAuth.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutAuth.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data)
            account.let {
                showProgress(true)
                viewModel.googleSignIn(it)
            }
        }
    }

}

const val REQUEST_CODE_SIGN_IN = 0