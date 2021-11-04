package com.example.tournamentmaker.mainactivity.mainfragments.ui.addtournament

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tournamentmaker.R
import com.example.tournamentmaker.databinding.FragmentAddTournamentBinding
import com.example.tournamentmaker.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect


class AddTournamentFragment : Fragment(R.layout.fragment_add_tournament) {
    private val viewModel: AddTournamentViewModel by viewModels()
    private lateinit var binding: FragmentAddTournamentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddTournamentBinding.bind(view)
        binding.apply {

            etTournamentName.setText(viewModel.name)
            btnSport.text = viewModel.sport
            btnType.text = viewModel.type
            etOtherSportName.setText(viewModel.otherSport)
            etTournamentPassword.setText(viewModel.tournamentPassword)
            etTournamentAccessPassword.setText(viewModel.tournamentAccessPassword)

            etTournamentName.addTextChangedListener {
                viewModel.name = it.toString()
            }

            btnSport.addTextChangedListener {
                viewModel.sport = it.toString()
            }

            btnType.addTextChangedListener {
                viewModel.type = it.toString()
            }

            etOtherSportName.addTextChangedListener {
                viewModel.otherSport = it.toString()
            }

            etTournamentPassword.addTextChangedListener {
                viewModel.tournamentPassword = it.toString()
            }

            etTournamentAccessPassword.addTextChangedListener {
                viewModel.tournamentAccessPassword = it.toString()
            }

            rbPublic.isChecked = true

            btnSport.setOnClickListener {
                var sport = ""
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Select Sport")
                builder.setItems(
                    sports
                ) { dialog, position ->
                    dialog.dismiss()
                    sport = sports[position]
                    btnSport.text = sport
                    if (sport == "Other") {
                        tournamentSportName.visibility = View.VISIBLE
                    } else {
                        tournamentSportName.visibility = View.GONE
                    }
                }
                builder.show()
            }

            btnType.setOnClickListener {
                var type = ""
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Select Type")
                builder.setItems(typeOf) { dialog, position ->
                    dialog.dismiss()
                    type = typeOf[position]
                    btnType.text = type
                }
                builder.show()
            }

            btnSave.setOnClickListener {
                showProgress(true)
                viewModel.addTournament()
            }

            rgTypeOfTournament.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId == rbPrivate.id) {
                    tournamentPassword.visibility = View.VISIBLE
                    viewModel.tournamentVisibility = "Private"
                } else {
                    tournamentPassword.visibility = View.GONE
                    viewModel.tournamentVisibility = "Public"
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addTournamentEvent.collect { event ->
                @Suppress("IMPLICIT_CAST_TO_ANY")
                when (event) {
                    is AddTournamentViewModel.AddTournamentEvent.NavigateBackWithResult -> {
                        showProgress(false)
                        findNavController().navigate(AddTournamentFragmentDirections.actionAddTournamentFragmentToHomeFragment())
                    }
                    is AddTournamentViewModel.AddTournamentEvent.ShowErrorMessage -> {
                        showProgress(false)
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }
    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressAddTournament.isVisible = bool
            if (bool) {
                parentLayoutAddTournament.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutAddTournament.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    private val sports = arrayOf(
        "Air Hockey",
        "American Football",
        "Aussie Rules",
        "Badminton",
        "Baseball",
        "Basketball",
        "Beach Volleyball",
        "Boxing",
        "Checkers",
        "Chess",
        "Cricket",
        "Darts",
        "Fencing",
        "Field Hockey",
        "Football",
        "Footvolley",
        "Gaming FIFA/PES",
        "Handball",
        "Ice Hockey",
        "Judo",
        "Kabaddi",
        "Karate",
        "Paintball",
        "Rugby",
        "Squash",
        "Table Tennis",
        "Taekwondo",
        "Tennis",
        "Volleyball",
        "Wrestling",
        "Other"
    )

    private val typeOf = arrayOf("Individual", "Teams/Doubles")
}