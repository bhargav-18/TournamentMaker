package com.example.tournamentmaker.data.entity

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
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
)
