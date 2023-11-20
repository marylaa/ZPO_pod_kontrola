package com.example.myapp.pills_list

data class PillModelNiestandardowo(
    var id: String? = null,
    var pacient: String? = null,
    var name: String = "",
    var availability: Int? = null,
    var inBox: Int? = null,
    var frequency: String = "",
    var time_list: MutableList<MutableList<MutableList<Any?>>?>? = null,
    var date_last: String = "",
    var date_next: String = ""

)
