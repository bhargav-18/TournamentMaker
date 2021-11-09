package com.example.tournamentmaker.mainactivity.mainfragments.ui.results

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tournamentmaker.R
import com.example.tournamentmaker.adapter.ResultsAdapter
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.databinding.FragmentResultsBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ResultsFragment : Fragment(R.layout.fragment_results) {

    private lateinit var binding: FragmentResultsBinding
    private lateinit var resultsAdapter: ResultsAdapter
    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")
    private val users = FirebaseFirestore.getInstance().collection("users")
    private val args: ResultsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentResultsBinding.bind(view)
        resultsAdapter = ResultsAdapter()

        getUpdatedList()

        setUpRecyclerView()

        resultsAdapter.setOnEitClickListener {
            findNavController().navigate(
                ResultsFragmentDirections.actionResultsFragmentToEditResultDialogFragment(
                    persons = it,
                    id = args.id
                )
            )
        }

    }

    private fun getUpdatedList() {

        CoroutineScope(Dispatchers.Main).launch {
            showProgress(true)

            val tournament =
                tournaments.document(args.id).get().await()
                    .toObject(Tournament::class.java)
            val resultList =
                tournament!!.matches as List<Map<String, Map<String, Map<String, String>>>>
            resultsAdapter.resultList = resultList

            showProgress(false)
        }

    }

    private fun setUpRecyclerView() {
        binding.rvTournamentResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = resultsAdapter
            itemAnimator = null
        }
    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressResults.isVisible = bool
            if (bool) {
                parentLayoutResults.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutResults.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

}