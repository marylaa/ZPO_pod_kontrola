package com.example.myapp.patient_notifications

data class NotificationModelAlert (
    var message: String = "",
    var pill: String = "",
    var date: String = "",
    var recipient: String = "",
    var id: String = ""
)