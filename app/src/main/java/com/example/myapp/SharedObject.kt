package com.example.myapp

object SharedObject {
    private var sortedDates = mutableMapOf<String, String>()
    private var wantedPill = ""

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
}
