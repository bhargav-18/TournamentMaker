package com.example.tournamentmaker.mainactivity.mainfragments.ui.results

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.tournamentmaker.R
import com.example.tournamentmaker.databinding.FragmentResultsBinding

class ResultsFragment : Fragment(R.layout.fragment_results) {

    private lateinit var binding: FragmentResultsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentResultsBinding.bind(view)
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