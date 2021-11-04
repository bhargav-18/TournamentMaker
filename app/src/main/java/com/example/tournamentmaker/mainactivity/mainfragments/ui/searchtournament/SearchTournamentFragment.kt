package com.example.tournamentmaker.mainactivity.mainfragments.ui.searchtournament

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tournamentmaker.R
import com.example.tournamentmaker.adapter.MyTournamentAdapter
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.databinding.FragmentSearchTournamentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SearchTournamentFragment : Fragment(R.layout.fragment_search_tournament) {

    private lateinit var myTournamentAdapter: MyTournamentAdapter
    private val viewModel: SearchTournamentViewModel by viewModels()
    private lateinit var binding: FragmentSearchTournamentBinding
    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchTournamentBinding.bind(view)
        myTournamentAdapter = MyTournamentAdapter("search")

        setUpRecyclerView()

        myTournamentAdapter.setOnTournamentClickListener {
            Toast.makeText(requireContext(), it.id, Toast.LENGTH_SHORT).show()
        }

        getUpdatedList()
    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressSearch.isVisible = bool
            if (bool) {
                parentLayoutSearch.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutSearch.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    private fun getUpdatedList() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val tournamentList =
                    tournaments.whereNotIn("host", arrayListOf(Firebase.auth.currentUser!!.uid))
                        .get().await().toObjects(Tournament::class.java)
                myTournamentAdapter.tournamentList = tournamentList
            } catch (e: Exception) {
                Snackbar.make(requireView(), e.message.toString(), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setUpRecyclerView() {
        binding.rvTournamentSearchList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myTournamentAdapter
            itemAnimator = null
        }
    }
}