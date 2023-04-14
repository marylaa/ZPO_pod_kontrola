package com.example.myapp

import java.util.UUID

data class PillModel (
    var id: UUID = UUID.randomUUID(),
    var pacient: String,
    var name: String,
    var availability: Integer,
    var frequency: String,
    var hour: String
)