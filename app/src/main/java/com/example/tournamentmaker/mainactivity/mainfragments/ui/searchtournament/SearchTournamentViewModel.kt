package com.example.tournamentmaker.mainactivity.mainfragments.ui.searchtournament

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamentmaker.data.entity.User
import com.example.tournamentmaker.mainactivity.MAIN_RESULT_OK
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class SearchTournamentViewModel : ViewModel() {

    private val searchTournamentEventChannel = Channel<SearchTournamentEvent>()
    val searchTournamentEvent = searchTournamentEventChannel.receiveAsFlow()
    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")
    private val users = FirebaseFirestore.getInstance().collection("users")

    fun joinTournament(id: String) {
        viewModelScope.launch(Dispatchers.IO) {

            val user = users.document(Firebase.auth.currentUser!!.uid).get().await()
                .toObject(User::class.java)

            if (user!!.tournamentsJoined.contains(id)) {
                showErrorMessage("You already joined the tournament")
                return@launch
            }

            try {
                tournaments.document(id)
                    .update("persons", FieldValue.arrayUnion(Firebase.auth.currentUser!!.uid))
                    .await()

                users.document(Firebase.auth.currentUser!!.uid)
                    .update("tournamentsJoined", FieldValue.arrayUnion(id))

                searchTournamentEventChannel.send(
                    SearchTournamentEvent.NavigateBackWithResult(
                        MAIN_RESULT_OK
                    )
                )

            } catch (e: Exception) {
                showErrorMessage(e.message.toString())
            }
        }
    }

    private fun showErrorMessage(text: String) = viewModelScope.launch {
        searchTournamentEventChannel.send(SearchTournamentEvent.ShowErrorMessage(text))
    }

    sealed class SearchTournamentEvent {
        data class ShowErrorMessage(val msg: String) : SearchTournamentEvent()
        data class NavigateBackWithResult(val result: Int) : SearchTournamentEvent()
    }
}