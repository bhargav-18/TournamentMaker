package com.example.tournamentmaker.mainactivity.mainfragments.ui.removetournamentdialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tournamentmaker.R
import com.example.tournamentmaker.databinding.RemoveTournamentDialogFragmentBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RemoveTournamentDialogFragment : DialogFragment(R.layout.remove_tournament_dialog_fragment) {

    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")
    private val users = FirebaseFirestore.getInstance().collection("users")
    private lateinit var binding: RemoveTournamentDialogFragmentBinding

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: RemoveTournamentDialogFragmentArgs by navArgs()
        val tournament = args.tournament
        binding = RemoveTournamentDialogFragmentBinding.bind(view)

        binding.apply {

            btnRemoveTournament.setOnClickListener {
                if (tournament?.tournamentAccessPassword == etRemoveTournamentPswd.text.toString()) {
                    CoroutineScope(Dispatchers.Main).launch {

                        try {
                            users.document(Firebase.auth.currentUser!!.uid)
                                .update(
                                    "tournamentsCreated",
                                    FieldValue.arrayRemove(tournament.id)
                                )
                                .await()
                            tournaments.document(tournament.id).delete().await()
                            findNavController().navigate(RemoveTournamentDialogFragmentDirections.actionRemoveTournamentDialogFragmentToHomeFragment())

                        } catch (e: Exception) {
                            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                            dialog?.dismiss()
                        }

                    }
                } else {
                    Toast.makeText(
                        context,
                        "Tournament Access Password is wrong.\nCheck password and try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}