package com.example.myapp.pills_list

data class PillModel (
    var id: String? = null,
    var pacient: String? = null,
    var name: String = "",
    var availability: Int? = null,
    var inBox: Int? = null,
    var frequency: String = "",
    var hour: Int? = null,
    var minute: Int? = null,
    var isChecked: Boolean = false // Define the isChecked property

)
