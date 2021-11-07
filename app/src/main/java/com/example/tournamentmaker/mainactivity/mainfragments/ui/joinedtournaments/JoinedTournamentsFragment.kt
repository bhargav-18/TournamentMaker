package com.example.tournamentmaker.mainactivity.mainfragments.ui.joinedtournaments

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tournamentmaker.R
import com.example.tournamentmaker.adapter.JoinedTournamentAdapter
import com.example.tournamentmaker.adapter.MyTournamentAdapter
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.databinding.FragmentJoinedTournamentsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class JoinedTournamentsFragment : Fragment(R.layout.fragment_joined_tournaments) {

    private lateinit var joinedTournamentAdapter: JoinedTournamentAdapter
    private lateinit var binding: FragmentJoinedTournamentsBinding
    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentJoinedTournamentsBinding.bind(view)
        joinedTournamentAdapter = JoinedTournamentAdapter()

        setUpRecyclerView()

        joinedTournamentAdapter.setOnTournamentClickListener {
            findNavController().navigate(
                JoinedTournamentsFragmentDirections.actionJoinedTournamentsFragmentToSetupTournamentFragment(
                    id = it.id,
                    title = it.tournamentName
                )
            )
        }

        getUpdatedList()

    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressJoined.isVisible = bool
            if (bool) {
                parentLayoutJoined.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutJoined.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    private fun getUpdatedList() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val tournamentList =
                    tournaments.whereArrayContains("persons", Firebase.auth.currentUser!!.uid).get()
                        .await().toObjects(Tournament::class.java)
                joinedTournamentAdapter.tournamentList = tournamentList
            } catch (e: Exception) {
                Snackbar.make(requireView(), e.message.toString(), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setUpRecyclerView() {
        binding.rvJoinedTournamentsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = joinedTournamentAdapter
            itemAnimator = null
        }
    }
}