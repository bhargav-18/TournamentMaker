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
    val scheduled: String = "",
    val maxPersons: Int = 0,
    val persons: ArrayList<String> = arrayListOf(),
    val host: String = "",
    val matches: ArrayList<Map<String, Map<String, Map<String, String>>>> = arrayListOf(),
    val results: Map<String, Map<String, Float>> = mapOf(),
    val winner: String = ""
)
