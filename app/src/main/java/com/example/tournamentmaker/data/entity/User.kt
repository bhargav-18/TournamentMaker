package com.example.tournamentmaker.data.entity

data class User(
    val uid: String = "",
    val userName: String = "",
    val email: String = "",
    val tournamentsCreated: ArrayList<String> = arrayListOf(),
    val tournamentsJoined: ArrayList<String> = arrayListOf()
)