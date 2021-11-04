package com.example.tournamentmaker.mainactivity.mainfragments.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamentmaker.adapter.MyTournamentAdapter
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.mainactivity.MAIN_RESULT_OK
import com.example.tournamentmaker.mainactivity.mainfragments.ui.addtournament.AddTournamentViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel : ViewModel() {
    private val homeEventChannel = Channel<HomeEvent>()
    val homeEvent = homeEventChannel.receiveAsFlow()
    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")
    private val users = FirebaseFirestore.getInstance().collection("users")

    fun deleteTournament(tournament: Tournament) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                users.document(Firebase.auth.currentUser!!.uid)
                    .update("tournamentsCreated", FieldValue.arrayRemove(tournament.id)).await()
                tournaments.document(tournament.id).delete()
                homeEventChannel.send(HomeEvent.NavigateBackWithResult(MAIN_RESULT_OK))
            } catch (e: Exception) {
                showErrorMessage(e.message.toString())
            }
        }
    }

    private fun showErrorMessage(text: String) = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.ShowErrorMessage(text))
    }

    sealed class HomeEvent {
        data class ShowErrorMessage(val msg: String) : HomeEvent()
        data class NavigateBackWithResult(val result: Int) : HomeEvent()
    }
}