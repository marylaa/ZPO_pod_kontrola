package com.example.myapp.pacients_list

import com.example.myapp.DoctorModel

data class PacientModel (
    var firstName: String = "",
    var lastName: String = "",
    var doctor: DoctorModel? = null
)
