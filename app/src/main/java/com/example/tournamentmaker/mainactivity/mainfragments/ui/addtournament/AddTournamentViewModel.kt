package com.example.tournamentmaker.mainactivity.mainfragments.ui.addtournament

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamentmaker.data.entity.Tournament
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

class AddTournamentViewModel constructor(
    private val state: SavedStateHandle
) : ViewModel() {
    private val addTournamentEventChannel = Channel<AddTournamentEvent>()
    val addTournamentEvent = addTournamentEventChannel.receiveAsFlow()
    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")
    private val users = FirebaseFirestore.getInstance().collection("users")

    var name = state.get<String>("name") ?: ""
        set(value) {
            field = value
            state.set("name", value)
        }

    var sport = state.get<String>("sport") ?: "Select Sport"
        set(value) {
            field = value
            state.set("sport", value)
        }

    var type = state.get<String>("type") ?: "Select Type"
        set(value) {
            field = value
            state.set("type", value)
        }

    var otherSport = state.get<String>("otherSport") ?: ""
        set(value) {
            field = value
            state.set("otherSport", value)
        }

    var tournamentPassword = state.get<String>("tournamentPassword") ?: ""
        set(value) {
            field = value
            state.set("tournamentPassword", value)
        }

    var tournamentAccessPassword = state.get<String>("tournamentAccessPassword") ?: ""
        set(value) {
            field = value
            state.set("tournamentAccessPassword", value)
        }

    var tournamentVisibility = state.get<String>("tournamentVisibility") ?: "Public"
        set(value) {
            field = value
            state.set("tournamentVisibility", value)
        }

    fun addTournament() {
        val error =
            if (name.isEmpty() || sport == "Select Sport" || type == "Select Type" || tournamentAccessPassword.isEmpty()) {
                "The field must not be empty"
            } else if (sport == "Other" && otherSport.isEmpty()) {
                "The field must not be empty"
            } else if (tournamentVisibility == "Private" && tournamentPassword.isEmpty()) {
                "The field must not be empty"
            } else null

        if (error != null) {
            showErrorMessage(error)
            return
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val id = tournaments.document().id
                val tournament = Tournament(
                    id = id,
                    tournamentName = name,
                    tournamentSport = sport,
                    tournamentType = type,
                    tournamentVisibility = tournamentVisibility,
                    tournamentPassword = tournamentPassword,
                    tournamentAccessPassword = tournamentAccessPassword,
                    host = Firebase.auth.currentUser?.uid.toString()
                )
                tournaments.document(id).set(tournament).await()
                users.document(Firebase.auth.currentUser!!.uid)
                    .update("tournamentsCreated", FieldValue.arrayUnion(id)).await()
                addTournamentEventChannel.send(
                    AddTournamentEvent.NavigateBackWithResult(
                        MAIN_RESULT_OK
                    )
                )
            }
        }
    }

    private fun showErrorMessage(text: String) = viewModelScope.launch {
        addTournamentEventChannel.send(AddTournamentEvent.ShowErrorMessage(text))
    }

    sealed class AddTournamentEvent {
        data class ShowErrorMessage(val msg: String) : AddTournamentEvent()
        data class NavigateBackWithResult(val result: Int) : AddTournamentEvent()
    }
}