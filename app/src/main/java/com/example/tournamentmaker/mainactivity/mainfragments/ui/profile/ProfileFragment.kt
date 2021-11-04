package com.example.tournamentmaker.mainactivity.mainfragments.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tournamentmaker.R
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.databinding.FragmentProfileBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProfileBinding.bind(view)

        binding.apply {

            val user = Firebase.auth.currentUser

            tvUserName.text = user?.displayName
            tvName.text = user?.displayName
            tvUserEmail.text = user?.email
            tvEmailId.text = user?.email

            CoroutineScope(Dispatchers.Main).launch {
                val tournament = tournaments.whereEqualTo("host", user?.uid).get().await()
                    .toObjects(Tournament::class.java)

                if (tournament.size >= 3) {
                    val tournaments =
                        "${tournament[0].tournamentName}\n${tournament[1].tournamentName}\n${tournament[2].tournamentName}\n....."
                    tvListTournament.text = tournaments
                } else {
                    var tournaments = ""
                    for (t in tournament) {
                        tournaments = tournaments + t.tournamentName + "\n"
                    }
                    tvListTournament.text = tournaments
                }
            }

            btnLogout.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToSignOutDialogFragment())
            }

            cvInfo.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToEditProfileDialogFragment())
            }

            cvTournamentCreated.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToHomeFragment())
            }
        }
    }
}