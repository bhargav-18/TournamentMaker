package com.example.tournamentmaker.mainactivity.mainfragments.ui.home

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tournamentmaker.R
import com.example.tournamentmaker.adapter.MyTournamentAdapter
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.databinding.FragmentHomeBinding
import com.example.tournamentmaker.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var myTournamentAdapter: MyTournamentAdapter
    private lateinit var binding: FragmentHomeBinding
    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)
        myTournamentAdapter = MyTournamentAdapter("my")

        setUpRecyclerView()

        myTournamentAdapter.setOnTournamentClickListener {

            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToSetupTournamentFragment(
                    id = it.id
                )
            )

        }

        myTournamentAdapter.setOnDeleteClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionGlobalRemoveTournamentDialogFragment(
                    id = it.id
                )
            )
        }
        getUpdatedList()


    }

    private fun getUpdatedList() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val tournamentList =
                    tournaments.whereEqualTo("host", Firebase.auth.currentUser!!.uid).get()
                        .await()
                        .toObjects(Tournament::class.java)
                myTournamentAdapter.tournamentList = tournamentList
            } catch (e: Exception) {
                Snackbar.make(requireView(), e.message.toString(), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setUpRecyclerView() {
        binding.rvTournamentList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myTournamentAdapter
            itemAnimator = null
        }
    }
}

