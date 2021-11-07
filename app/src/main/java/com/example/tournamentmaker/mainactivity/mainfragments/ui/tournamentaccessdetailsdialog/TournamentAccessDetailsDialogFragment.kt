package com.example.tournamentmaker.mainactivity.mainfragments.ui.tournamentaccessdetailsdialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.example.tournamentmaker.R
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.databinding.TournamentAccessDetailsDialogBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TournamentAccessDetailsDialogFragment :
    DialogFragment(R.layout.tournament_access_details_dialog) {

    private lateinit var binding: TournamentAccessDetailsDialogBinding
    private lateinit var tournament: Tournament
    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: TournamentAccessDetailsDialogFragmentArgs by navArgs()
        val id = args.id

        binding = TournamentAccessDetailsDialogBinding.bind(view)

        binding.apply {

            CoroutineScope(Dispatchers.Main).launch {

                tournament =
                    tournaments.document(id).get().await().toObject(Tournament::class.java)!!

                if (tournament.tournamentVisibility == "Private") {
                    tvTournamentDetailsPassword.visibility = View.VISIBLE
                } else {
                    tvTournamentDetailsPassword.visibility = View.GONE
                }

                tvTournamentDetailsName.text = tournament.tournamentName
                tvTournamentDetailsId.text = "Tournament ID:\n" + tournament.id
                tvTournamentDetailsPassword.text = "Password:\n" + tournament.tournamentPassword
                tvTournamentDetailsAccessPassword.text =
                    "Access Password:\n" + tournament.tournamentAccessPassword

            }
        }
    }
}