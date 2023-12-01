package com.example.myapp.notifications

data class NotificationModelAlert (
    var message: String = "",
    var pill: String = "",
    var date: String = "",
    var recipient: String = "",
    var id: String = "",
    var seen: Boolean = false,
    var pacient: String = ""

)