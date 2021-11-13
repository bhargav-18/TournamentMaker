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
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.databinding.RemoveTournamentDialogFragmentBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RemoveTournamentDialogFragment : DialogFragment(R.layout.remove_tournament_dialog_fragment) {

    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")
    private val users = FirebaseFirestore.getInstance().collection("users")
    private lateinit var binding: RemoveTournamentDialogFragmentBinding
    private lateinit var tournament: Tournament

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
        val id = args.id


        CoroutineScope(Dispatchers.IO).launch {
            tournament = tournaments.document(id).get().await().toObject(Tournament::class.java)!!
        }
        binding = RemoveTournamentDialogFragmentBinding.bind(view)

        binding.apply {

            btnRemoveTournament.setOnClickListener {

                if (tournament.tournamentAccessPassword == etRemoveTournamentPswd.text.toString()) {

                    CoroutineScope(Dispatchers.IO).launch {

                        try {
                            users.document(Firebase.auth.currentUser!!.uid)
                                .update(
                                    "tournamentsCreated",
                                    FieldValue.arrayRemove(id)
                                )
                                .await()

                            for (user in tournament.persons) {
                                users.document(user).update(
                                    "tournamentsJoined",
                                    FieldValue.arrayRemove(id)
                                )
                            }

                            tournaments.document(id).delete().await()

                            withContext(Dispatchers.Main) {

                                findNavController().navigate(
                                    RemoveTournamentDialogFragmentDirections.actionRemoveTournamentDialogFragmentToHomeFragment()
                                )

                            }


                        } catch (e: Exception) {

                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT)
                                    .show()
                                dialog?.dismiss()
                            }

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