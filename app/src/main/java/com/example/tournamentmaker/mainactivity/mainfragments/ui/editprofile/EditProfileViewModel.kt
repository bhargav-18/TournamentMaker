package com.example.tournamentmaker.mainactivity.mainfragments.ui.editprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamentmaker.mainactivity.MAIN_RESULT_OK
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val state: SavedStateHandle
) : ViewModel() {

    private val editProfileEventChannel = Channel<EditProfileEvent>()
    val editProfileEvent = editProfileEventChannel.receiveAsFlow()
    private val users = FirebaseFirestore.getInstance().collection("users")

    var username = state.get<String>("username") ?: ""
        set(value) {
            field = value
            state.set("username", value)
        }

    var oldPassword = state.get<String>("oldPassword") ?: ""
        set(value) {
            field = value
            state.set("oldPassword", value)
        }

    var newPassword = state.get<String>("newPassword") ?: ""
        set(value) {
            field = value
            state.set("newPassword", value)
        }

    fun changePassword() {
        val user = Firebase.auth.currentUser

        val credential = EmailAuthProvider
            .getCredential(user?.email!!, oldPassword)

        viewModelScope.launch(Dispatchers.IO) {
            user.reauthenticate(credential).addOnCompleteListener {
                if (it.isSuccessful) {
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                navigateBackWithResult()
                            } else {
                                showErrorMessage(task.exception.toString())
                            }
                        }

                } else {
                    showErrorMessage(it.exception.toString())
                }
            }
        }
    }

    fun updateUserName() {
        val user = Firebase.auth.currentUser

        if (username.isEmpty()) {
            showErrorMessage("Username field cannot be empty")
        } else {

            viewModelScope.launch(Dispatchers.IO) {

                val profileUpdate =
                    UserProfileChangeRequest.Builder().setDisplayName(username).build()

                user?.updateProfile(profileUpdate)?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        users.document(user.uid).update("userName", username)
                        navigateBackWithResult()
                    } else {
                        showErrorMessage(it.exception.toString())
                    }
                }
            }
        }
    }

    private fun showErrorMessage(text: String) = viewModelScope.launch {
        editProfileEventChannel.send(EditProfileEvent.ShowErrorMessage(text))
    }

    private fun navigateBackWithResult() = viewModelScope.launch {
        editProfileEventChannel.send(EditProfileEvent.NavigateBackWithResult(MAIN_RESULT_OK))
    }

    sealed class EditProfileEvent {
        data class ShowErrorMessage(val msg: String) : EditProfileEvent()
        data class NavigateBackWithResult(val result: Int) : EditProfileEvent()
    }
}