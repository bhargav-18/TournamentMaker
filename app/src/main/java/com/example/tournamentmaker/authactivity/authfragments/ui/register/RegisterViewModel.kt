package com.example.tournamentmaker.authactivity.authfragments.ui.register

import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamentmaker.authactivity.AUTH_RESULT_OK
import com.example.tournamentmaker.data.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel constructor(
    private val state: SavedStateHandle
) : ViewModel() {
    private val registerEventChannel = Channel<RegisterEvent>()
    val registerEvent = registerEventChannel.receiveAsFlow()
    private val auth = FirebaseAuth.getInstance()
    private val users = FirebaseFirestore.getInstance().collection("users")

    var email = state.get<String>("email") ?: ""
        set(value) {
            field = value
            state.set("email", value)
        }

    var password = state.get<String>("password") ?: ""
        set(value) {
            field = value
            state.set("password", value)
        }

    var username = state.get<String>("uname") ?: ""
        set(value) {
            field = value
            state.set("uname", value)
        }

    var repeatedPassword = state.get<String>("cpassword") ?: ""
        set(value) {
            field = value
            state.set("cpassword", value)
        }

    fun register() {
        val error = if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            "The field must not be empty"
        } else if (password != repeatedPassword) {
            "Password and confirm password doesn't match"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Please enter a valid email"
        } else null

        if (error != null) {
            showErrorMessage(error)
        } else {
            viewModelScope.launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                } catch (e: Exception) {
                    showErrorMessage(e.message.toString())
                    return@launch
                }
                if (auth.currentUser != null) {
                    val uid = auth.currentUser?.uid!!
                    val user = User(uid, username, email)
                    users.document(uid).set(user).await()

                    auth.currentUser?.updateProfile(
                        userProfileChangeRequest {
                            displayName = username
                        }
                    )

                    registerEventChannel.send(RegisterEvent.NavigateBackWithResult(AUTH_RESULT_OK))
                } else {
                    showErrorMessage("Something went wrong. Please try again later")
                }

            }

        }
    }

    private fun showErrorMessage(text: String) = viewModelScope.launch {
        registerEventChannel.send(RegisterEvent.ShowErrorMessage(text))
    }

    sealed class RegisterEvent {
        data class ShowErrorMessage(val msg: String) : RegisterEvent()
        data class NavigateBackWithResult(val result: Int) : RegisterEvent()
    }
}