package com.example.tournamentmaker.mainactivity.mainfragments.ui.tournamentaccessdetailsdialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.example.tournamentmaker.R
import com.example.tournamentmaker.databinding.TournamentAccessDetailsDialogBinding

class TournamentAccessDetailsDialogFragment :
    DialogFragment(R.layout.tournament_access_details_dialog) {

    private lateinit var binding: TournamentAccessDetailsDialogBinding

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
        val tournament = args.tournament

        binding = TournamentAccessDetailsDialogBinding.bind(view)

        binding.apply {

            if (tournament?.tournamentVisibility == "Private") {
                tvTournamentDetailsPassword.visibility = View.VISIBLE
            } else {
                tvTournamentDetailsPassword.visibility = View.GONE
            }

            tvTournamentDetailsName.text = tournament?.tournamentName
            tvTournamentDetailsId.text = "Tournament ID:\n" + tournament?.id
            tvTournamentDetailsPassword.text = "Password:\n" + tournament?.tournamentPassword
            tvTournamentDetailsAccessPassword.text =
                "Access Password:\n" + tournament?.tournamentAccessPassword
        }
    }
}