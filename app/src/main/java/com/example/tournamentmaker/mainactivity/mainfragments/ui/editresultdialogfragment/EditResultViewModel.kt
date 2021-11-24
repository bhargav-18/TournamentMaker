package com.example.tournamentmaker.mainactivity.mainfragments.ui.editresultdialogfragment

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.data.entity.User
import com.example.tournamentmaker.mainactivity.MAIN_RESULT_OK
import com.example.tournamentmaker.notification.NotificationData
import com.example.tournamentmaker.notification.NotificationService
import com.example.tournamentmaker.notification.PushNotification
import com.example.tournamentmaker.notification.RetrofitInstance
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditResultViewModel(
    private val state: SavedStateHandle
) : ViewModel() {

    private val editResultEventChannel = Channel<EditResultEvent>()
    val editResultEvent = editResultEventChannel.receiveAsFlow()
    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")
    private val users = FirebaseFirestore.getInstance().collection("users")


    var winner = state.get<String>("winner") ?: ""
        set(value) {
            field = value
            state.set("winner", value)
        }

    var scoreP1 = state.get<String>("scoreP1") ?: ""
        set(value) {
            field = value
            state.set("scoreP1", value)
        }

    var scoreP2 = state.get<String>("scoreP2") ?: ""
        set(value) {
            field = value
            state.set("scoreP2", value)
        }


    fun updateResult(id: String, person1: String, person2: String, position: Int) {

        val error = if (winner.isBlank()) {
            "Please select the winner"
        } else if (scoreP1.isBlank() || scoreP2.isBlank()) {
            "Please enter tie breaker points"
        } else {
            null
        }

        if (error != null) {
            showErrorMessage(error)
            return
        } else {
            viewModelScope.launch(Dispatchers.IO) {

                val map: Map<String, Map<String, Map<String, String>>> =
                    mapOf(person1 to mapOf(person2 to mapOf("winner" to winner)))

                val tournament =
                    tournaments.document(id).get().await()
                        .toObject(Tournament::class.java)!!

                val matchesArray = tournament.matches

                matchesArray.removeAt(position)
                matchesArray.add(position, map)

                tournaments.document(id)
                    .update("matches", matchesArray).await()

                tournaments.document(id)
                    .update("results.${person1}.played", FieldValue.increment(1))

                tournaments.document(id)
                    .update("results.${person2}.played", FieldValue.increment(1))


                when (winner) {
                    person1 -> {
                        tournaments.document(id)
                            .update("results.${person1}.won", FieldValue.increment(1))
                            .await()

                        tournaments.document(id)
                            .update("results.${person2}.lost", FieldValue.increment(1))
                            .await()
                    }
                    "Draw" -> {
                        tournaments.document(id)
                            .update("results.${person1}.draw", FieldValue.increment(1))
                            .await()

                        tournaments.document(id)
                            .update("results.${person2}.draw", FieldValue.increment(1))
                            .await()
                    }
                    else -> {
                        tournaments.document(id)
                            .update("results.${person1}.lost", FieldValue.increment(1))
                            .await()

                        tournaments.document(id)
                            .update("results.${person2}.won", FieldValue.increment(1))
                            .await()
                    }
                }

                tournaments.document(id)
                    .update(
                        "results.${person1}.tieBreaker",
                        FieldValue.increment(scoreP1.toLong())
                    ).await()

                tournaments.document(id)
                    .update(
                        "results.${person2}.tieBreaker",
                        FieldValue.increment(scoreP2.toLong())
                    ).await()

                val persons = tournament.persons

                for (p in persons) {
                    val token = users.document(p).get().await().toObject(User::class.java)!!.token

                    if (p == Firebase.auth.currentUser!!.uid) {
                        PushNotification(
                            NotificationData("A result has came", "Come back to check the winner"),
                            token
                        ).also {
                            sendNotification(it)
                        }
                    }
                }

                editResultEventChannel.send(EditResultEvent.NavigateBackWithResult(MAIN_RESULT_OK))

            }
        }
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    Log.d("TAG_EDIT_RESULT", "Response: ${Gson().toJson(response)}")
                } else {
                    showErrorMessage(response.errorBody().toString())
                }
            } catch (e: Exception) {
                showErrorMessage(e.message.toString())
            }
        }


    private fun showErrorMessage(text: String) = viewModelScope.launch {
        editResultEventChannel.send(EditResultEvent.ShowErrorMessage(text))
    }

    sealed class EditResultEvent {
        data class ShowErrorMessage(val msg: String) : EditResultEvent()
        data class NavigateBackWithResult(val result: Int) : EditResultEvent()
    }

}