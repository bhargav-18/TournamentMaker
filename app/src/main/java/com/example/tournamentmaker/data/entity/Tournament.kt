package com.example.tournamentmaker.data.entity

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Tournament(
    val id: String = "",
    val tournamentName: String = "",
    val tournamentSport: String = "",
    val tournamentType: String = "",
    val tournamentVisibility: String = "",
    val tournamentPassword: String = "",
    val tournamentAccessPassword: String = "",
    val scheduled: Boolean = false,
    val maxPersons: Int = 0,
    val persons: ArrayList<String?> = arrayListOf(),
    val host: String = ""
) : Parcelable
