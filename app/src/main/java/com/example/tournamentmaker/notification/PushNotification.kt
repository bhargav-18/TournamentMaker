package com.example.tournamentmaker.notification

data class PushNotification(
    val data: NotificationData,
    val to: String
)