package com.example.myapp.pills_list

data class PillModelCustom(
    var id: String? = null,
    var pacient: String? = null,
    var name: String = "",
    var availability: Int? = null,
    var inBox: Int? = null,
    var frequency: String = "",
    var time_list: MutableList<HashMap<String, Any>>? = null,
    var date_last: String = "",
    var date_next: String = ""

)
