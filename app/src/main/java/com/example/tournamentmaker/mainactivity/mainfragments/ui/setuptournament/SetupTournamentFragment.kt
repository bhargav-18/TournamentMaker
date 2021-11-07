package com.example.tournamentmaker.mainactivity.mainfragments.ui.setuptournament

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tournamentmaker.R
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.databinding.FragmentSetupTournamentBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SetupTournamentFragment : Fragment(R.layout.fragment_setup_tournament) {

    private lateinit var binding: FragmentSetupTournamentBinding
    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")
    private lateinit var tournament: Tournament

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: SetupTournamentFragmentArgs by navArgs()

        binding = FragmentSetupTournamentBinding.bind(view)


        binding.apply {


            CoroutineScope(Dispatchers.Main).launch {

                showProgress(true)

                tournament =
                    tournaments.document(args.id).get().await().toObject(Tournament::class.java)!!

                if (tournament.host != Firebase.auth.currentUser!!.uid) {
                    btnTournamentAccess.visibility = View.GONE
                    btnRemove.visibility = View.GONE
                }
                if (tournament.matches.size > 0) {
                    btnResults.visibility = View.VISIBLE
                    btnStandings.visibility = View.VISIBLE
                    btnCreateMatches.visibility = View.GONE
                    btnManageParticipants.text = "Participants"
                    btnMatches.visibility = View.VISIBLE
                } else {
                    btnResults.visibility = View.GONE
                    btnStandings.visibility = View.GONE
                    btnCreateMatches.visibility = View.VISIBLE
                    btnMatches.visibility = View.GONE
                }

                showProgress(false)
            }

            btnCreateMatches.setOnClickListener {
                if (tournament.persons.size < 2) {
                    Toast.makeText(
                        context,
                        "There should be atleast 2 persons to start tournament",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                } else {
                    tournaments.document(tournament.id).update("scheduled", "Started")
                    findNavController().navigate(
                        SetupTournamentFragmentDirections.actionSetupTournamentFragmentToCreateMatchesFragment(
                            id = tournament.id,
                            title = tournament.tournamentName
                        )
                    )
                }

            }

            btnManageParticipants.setOnClickListener {
                findNavController().navigate(
                    SetupTournamentFragmentDirections.actionSetupTournamentFragmentToManageParticipantsFragment(
                        id = tournament.id
                    )
                )
            }

            btnMatches.setOnClickListener {
                findNavController().navigate(
                    SetupTournamentFragmentDirections.actionSetupTournamentFragmentToMatchesFragment(
                        id = tournament.id,
                        title = tournament.tournamentName
                    )
                )
            }

            btnRemove.setOnClickListener {
                findNavController().navigate(
                    SetupTournamentFragmentDirections.actionGlobalRemoveTournamentDialogFragment(
                        id = tournament.id
                    )
                )
            }

            btnResults.setOnClickListener {
                findNavController().navigate(SetupTournamentFragmentDirections.actionSetupTournamentFragmentToResultsFragment())
            }

            btnStandings.setOnClickListener {
                findNavController().navigate(SetupTournamentFragmentDirections.actionSetupTournamentFragmentToStandingsFragment())
            }

            btnTournamentAccess.setOnClickListener {
                findNavController().navigate(
                    SetupTournamentFragmentDirections.actionSetupTournamentFragmentToTournamentAccessDetailsDialogFragment(
                        id = tournament.id
                    )
                )
            }

        }
    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressSetup.isVisible = bool
            if (bool) {
                parentLayoutSetup.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutSetup.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

}