package com.example.tournamentmaker.mainactivity.mainfragments.ui.searchtournament

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tournamentmaker.R
import com.example.tournamentmaker.adapter.MyTournamentAdapter
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.databinding.FragmentSearchTournamentBinding
import com.example.tournamentmaker.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
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
            Toast.makeText(context, it.id, Toast.LENGTH_SHORT).show()
        }

        myTournamentAdapter.setOnJoinClickListener { tournament ->

            if (!tournament.scheduled) {
                Toast.makeText(
                    context,
                    "Tournament is not scheduled yet. \nPlease try again later.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (tournament.persons.size >= tournament.maxPersons) {
                Toast.makeText(
                    context,
                    "Tournament is full. \nTry joining another tournament.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (tournament.tournamentVisibility == "Private") {

                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.private_tournament_join_dialog)
                val etJoinPrvt = dialog.findViewById<TextInputEditText>(R.id.et_private_join_pswd)
                val btnJoinPrvt = dialog.findViewById<AppCompatButton>(R.id.btn_join_prvt)

                btnJoinPrvt.setOnClickListener {
                    if (tournament.tournamentPassword == etJoinPrvt.text.toString()) {
                        dialog.dismiss()
                        showProgress(true)
                        viewModel.joinTournament(tournament.id)
                    } else {
                        Toast.makeText(context, "Password is incorrect!", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.show()

            } else {
                showProgress(true)
                viewModel.joinTournament(tournament.id)
            }
        }

        getUpdatedList()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.searchTournamentEvent.collect { event ->
                @Suppress("IMPLICIT_CAST_TO_ANY")
                when (event) {
                    is SearchTournamentViewModel.SearchTournamentEvent.NavigateBackWithResult -> {
                        getUpdatedList()
                        showProgress(false)
                        findNavController().navigate(SearchTournamentFragmentDirections.actionSearchTournamentFragmentToJoinedTournamentsFragment())
                    }
                    is SearchTournamentViewModel.SearchTournamentEvent.ShowErrorMessage -> {
                        showProgress(false)
                        getUpdatedList()
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }
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
        showProgress(true)
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
        showProgress(false)
    }

    private fun setUpRecyclerView() {
        binding.rvTournamentSearchList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myTournamentAdapter
            itemAnimator = null
        }
    }
}