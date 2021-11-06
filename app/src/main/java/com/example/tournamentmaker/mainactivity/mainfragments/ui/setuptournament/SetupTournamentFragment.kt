package com.example.tournamentmaker.mainactivity.mainfragments.ui.setuptournament

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tournamentmaker.R
import com.example.tournamentmaker.databinding.FragmentSetupTournamentBinding

class SetupTournamentFragment : Fragment(R.layout.fragment_setup_tournament) {

    private lateinit var binding: FragmentSetupTournamentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: SetupTournamentFragmentArgs by navArgs()

        binding = FragmentSetupTournamentBinding.bind(view)

        binding.apply {

            if (args.tournament.scheduled) {
                btnResults.visibility = View.VISIBLE
                btnStandings.visibility = View.VISIBLE
            } else {
                btnResults.visibility = View.GONE
                btnStandings.visibility = View.GONE
            }

            btnCreateMatches.setOnClickListener {
                findNavController().navigate(SetupTournamentFragmentDirections.actionSetupTournamentFragmentToCreateMatchesFragment())
            }

            btnManageParticipants.setOnClickListener {
                findNavController().navigate(SetupTournamentFragmentDirections.actionSetupTournamentFragmentToManageParticipantsFragment())
            }

            btnRemove.setOnClickListener {
                findNavController().navigate(
                    SetupTournamentFragmentDirections.actionGlobalRemoveTournamentDialogFragment(
                        tournament = args.tournament
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
                        tournament = args.tournament
                    )
                )
            }

        }
    }
}