package com.example.tournamentmaker.mainactivity.mainfragments.ui.standings

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.tournamentmaker.R
import com.example.tournamentmaker.databinding.FragmentStandingsBinding

class StandingsFragment : Fragment(R.layout.fragment_standings) {

    private lateinit var binding: FragmentStandingsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentStandingsBinding.bind(view)
    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressStandings.isVisible = bool
            if (bool) {
                parentLayoutStandings.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutStandings.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }
}