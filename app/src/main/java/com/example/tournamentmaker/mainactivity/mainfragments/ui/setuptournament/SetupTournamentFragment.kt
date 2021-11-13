package com.example.tournamentmaker.mainactivity.mainfragments.ui.setuptournament

import android.app.Activity
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
import com.example.tournamentmaker.util.hideKeyboard
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SetupTournamentFragment : Fragment(R.layout.fragment_setup_tournament) {

    private lateinit var binding: FragmentSetupTournamentBinding
    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")
    private lateinit var tournament: Tournament

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: SetupTournamentFragmentArgs by navArgs()

        binding = FragmentSetupTournamentBinding.bind(view)


        binding.apply {


            CoroutineScope(Dispatchers.IO).launch {

                withContext(Dispatchers.Main) {
                    showProgress(true)
                }


                tournament =
                    tournaments.document(args.id).get().await().toObject(Tournament::class.java)!!


                withContext(Dispatchers.Main) {

                    etEditNoOfPersons.setText(tournament.maxPersons.toString())

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
                    if (tournament.host != Firebase.auth.currentUser!!.uid) {
                        btnTournamentAccess.visibility = View.GONE
                        btnRemove.visibility = View.GONE
                        btnCreateMatches.visibility = View.GONE
                        numberOfPersons.visibility = View.GONE
                        btnUpdateNoOfPerson.visibility = View.GONE
                    }

                    showProgress(false)
                }

            }

            btnUpdateNoOfPerson.setOnClickListener {

                hideKeyboard(activity as Activity)

                showProgress(true)

                if (etEditNoOfPersons.text!!.isBlank()) {

                    showProgress(false)

                    Toast.makeText(
                        context,
                        "Please enter max number of persons for tournament",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                } else if (etEditNoOfPersons.text.toString().toInt() <= tournament.maxPersons) {

                    showProgress(false)

                    Toast.makeText(
                        context,
                        "Max persons cannot field cannot be decreased.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                CoroutineScope(Dispatchers.IO).launch {

                    tournaments.document(args.id)
                        .update("maxPersons", etEditNoOfPersons.text.toString().toInt()).await()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Max persons updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
                    CoroutineScope(Dispatchers.IO).launch {
                        tournaments.document(tournament.id).update("scheduled", "Started").await()

                        withContext(Dispatchers.Main) {
                            findNavController().navigate(
                                SetupTournamentFragmentDirections.actionSetupTournamentFragmentToCreateMatchesFragment(
                                    id = tournament.id,
                                    title = tournament.tournamentName
                                )
                            )
                        }
                    }
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
                findNavController().navigate(
                    SetupTournamentFragmentDirections.actionSetupTournamentFragmentToResultsFragment(
                        id = tournament.id
                    )
                )
            }

            btnStandings.setOnClickListener {
                findNavController().navigate(
                    SetupTournamentFragmentDirections.actionSetupTournamentFragmentToStandingsFragment(
                        id = tournament.id
                    )
                )
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