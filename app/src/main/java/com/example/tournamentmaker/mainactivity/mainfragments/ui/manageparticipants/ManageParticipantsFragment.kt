package com.example.tournamentmaker.mainactivity.mainfragments.ui.manageparticipants

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tournamentmaker.R
import com.example.tournamentmaker.adapter.ParticipantsAdapter
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.databinding.FragmentManageParticipantsBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ManageParticipantsFragment : Fragment(R.layout.fragment_manage_participants) {

    private lateinit var binding: FragmentManageParticipantsBinding
    private lateinit var participantsAdapter: ParticipantsAdapter
    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")
    private val users = FirebaseFirestore.getInstance().collection("users")
    val args: ManageParticipantsFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentManageParticipantsBinding.bind(view)
        participantsAdapter = ParticipantsAdapter()

        getUpdatedList()

        setUpRecyclerView()

        participantsAdapter.setOnPersonClickListener {

        }

        participantsAdapter.setOnRemoveClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    tournaments.document(args.tournament.id)
                        .update("persons", FieldValue.arrayRemove(it)).await()

                    users.document(it)
                        .update("tournamentsJoined", FieldValue.arrayRemove(args.tournament.id))
                        .await()

                } catch (e: Exception) {
                    Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            getUpdatedList()

        }

    }

    private fun getUpdatedList() {

        CoroutineScope(Dispatchers.Main).launch {
            val tournament =
                tournaments.document(args.tournament.id).get().await()
                    .toObject(Tournament::class.java)
            val personsList = tournament!!.persons as List<String>
            participantsAdapter.personList = personsList
        }

    }

    private fun setUpRecyclerView() {
        binding.rvParticipants.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = participantsAdapter
            itemAnimator = null
        }
    }

}