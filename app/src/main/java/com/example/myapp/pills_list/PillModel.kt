package com.example.myapp.pills_list

data class PillModel(
    var id: String? = null,
    var pacient: String? = null,
    var name: String = "",
    var availability: Int? = null,
    var inBox: Int? = null,
    var frequency: String = "",
    var time_list: List<List<Any>>? = null,
    var date: String = ""

)
