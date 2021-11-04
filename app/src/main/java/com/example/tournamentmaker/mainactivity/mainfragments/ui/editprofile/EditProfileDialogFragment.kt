package com.example.tournamentmaker.mainactivity.mainfragments.ui.editprofile

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tournamentmaker.R
import com.example.tournamentmaker.databinding.EditProfileDialogBinding
import com.example.tournamentmaker.util.exhaustive
import com.example.tournamentmaker.util.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collect

class EditProfileDialogFragment : DialogFragment(R.layout.edit_profile_dialog) {

    private lateinit var binding: EditProfileDialogBinding
    private val viewModel: EditProfileViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = EditProfileDialogBinding.bind(view)

        binding.apply {

            etEditUserName.setText(Firebase.auth.currentUser?.displayName)
            etEditUserPassword.setText(viewModel.oldPassword)
            etEditUserNewPassword.setText(viewModel.newPassword)

            etEditUserName.addTextChangedListener {
                viewModel.username = it.toString()
            }
            etEditUserPassword.addTextChangedListener {
                viewModel.oldPassword = it.toString()
            }
            etEditUserNewPassword.addTextChangedListener {
                viewModel.newPassword = it.toString()
            }

            btnChangePassword.setOnClickListener {
                hideKeyboard(activity as Activity)
                showProgress(true)
                viewModel.changePassword()
            }

            btnEditUsername.setOnClickListener {
                showProgress(true)
                hideKeyboard(activity as Activity)
                viewModel.updateUserName()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.editProfileEvent.collect { event ->
                when (event) {
                    is EditProfileViewModel.EditProfileEvent.NavigateBackWithResult -> {
                        showProgress(false)
                        findNavController().navigate(EditProfileDialogFragmentDirections.actionGlobalProfileFragment())
                    }
                    is EditProfileViewModel.EditProfileEvent.ShowErrorMessage -> {
                        showProgress(false)
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }
    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressEditProfile.isVisible = bool
            if (bool) {
                parentLayoutEditProfile.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutEditProfile.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }
}