package com.example.tournamentmaker.data.entity

data class Tournament(
    val tournamentName: String,
    val tournamentSport: String,
    val tournamentType: String,
    val tournamentVisibility: String,
    val tournamentPassword: String = "",
    val tournamentAccessPassword: String,
    val scheduled: Boolean = false,
    val numberOfPerson: Int = 0,
    val persons: ArrayList<String?> = arrayListOf(),
    val hosts: ArrayList<String?>
)
