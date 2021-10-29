package com.example.tournamentmaker.authactivity.authfragments.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamentmaker.authactivity.AUTH_RESULT_OK
import com.example.tournamentmaker.data.entity.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val authEventChannel = Channel<AuthEvent>()
    val authEvent = authEventChannel.receiveAsFlow()
    private val auth = FirebaseAuth.getInstance()
    private val users = FirebaseFirestore.getInstance().collection("users")

    fun googleSignIn(account: Task<GoogleSignInAccount>) {
        account.let {
            viewModelScope.launch {
                try {
                    val credentials =
                        GoogleAuthProvider.getCredential(it.result.idToken, null)
                    val result = auth.signInWithCredential(credentials).await()
                    val uid = result.user?.uid!!
                    val username = result.user?.displayName.toString()
                    val email = result.user?.email.toString()
                    val user = User(uid, username, email)
                    users.document(uid).set(user)
                    authEventChannel.send(AuthEvent.NavigateBackWithResult(AUTH_RESULT_OK))
                } catch (e: Exception) {
                    showErrorMessage("Please select an Account")
                }
            }
        }
    }

    private fun showErrorMessage(text: String) = viewModelScope.launch {
        authEventChannel.send(AuthEvent.ShowErrorMessage(text))
    }

    sealed class AuthEvent {
        data class ShowErrorMessage(val msg: String) : AuthEvent()
        data class NavigateBackWithResult(val result: Int) : AuthEvent()
    }
}