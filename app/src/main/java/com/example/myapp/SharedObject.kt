package com.example.myapp

object SharedObject {
    private var sortedDates = mutableMapOf<String, String>()
    private var wantedPill = ""
    private var pacientId = ""
    private var listPacientIds: Array<String> = emptyArray()

    fun getSortedDates(): MutableMap<String, String> {
        return sortedDates
    }

    fun setSortedDates(map: MutableMap<String, String>) {
        sortedDates = map
    }

    fun getWantedPill(): String{
        return wantedPill
    }

    fun setWantedPill(pill: String){
        wantedPill = pill
    }

    fun setPacientId(pacient: String){
        pacientId = pacient
    }

    fun getPacientId(): String{
        return pacientId
    }

    fun setlistPacientIds(pacients: Array<String>){
        listPacientIds = pacients
    }

    fun getlistPacientIds(): Array<String>{
        return listPacientIds
    }
}
