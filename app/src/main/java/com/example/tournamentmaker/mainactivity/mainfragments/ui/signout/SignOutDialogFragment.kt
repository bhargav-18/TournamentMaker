package com.example.tournamentmaker.mainactivity.mainfragments.ui.signout

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.tournamentmaker.authactivity.AuthActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignOutDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Sign out?")
            .setMessage("Do you wan to sign out from app?")
            .setPositiveButton("Yes") { _, _ ->
                CoroutineScope(Dispatchers.Main).launch {
//                    val option =
//                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
//                            .build()
//                    val signInClient = GoogleSignIn.getClient(requireActivity(), option)
//                    signInClient.signOut().addOnCompleteListener {
//                        if (it.isSuccessful) {
//
//                            Intent(requireContext(), AuthActivity::class.java).also { intent ->
//                                startActivity(intent)
//                                requireActivity().finish()
//
//                            }
//                        } else {
                    Firebase.auth.signOut()

                    Intent(requireContext(), AuthActivity::class.java).also { intent ->
                        startActivity(intent)
                        requireActivity().finish()

//                            }
//                        }
                    }
                }
            }.create()
}