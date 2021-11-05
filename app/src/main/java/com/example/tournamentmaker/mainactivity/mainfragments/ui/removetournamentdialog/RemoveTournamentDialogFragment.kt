package com.example.tournamentmaker.mainactivity.mainfragments.ui.removetournamentdialog

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RemoveTournamentDialogFragment : DialogFragment() {

    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")
    private val users = FirebaseFirestore.getInstance().collection("users")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val args: RemoveTournamentDialogFragmentArgs by navArgs()
        val id = args.id

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Delete Tournament?")
            .setMessage("Do you want to delete tournament")
            .setPositiveButton("Yes", null).create()

        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {

                    try {
                        users.document(Firebase.auth.currentUser!!.uid)
                            .update("tournamentsCreated", FieldValue.arrayRemove(id))
                            .await()
                        tournaments.document(id.toString()).delete().await()
                        findNavController().navigate(RemoveTournamentDialogFragmentDirections.actionRemoveTournamentDialogFragmentToHomeFragment())

                    } catch (e: Exception) {
                        Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }

                }
            }
        }
        return dialog
    }
}