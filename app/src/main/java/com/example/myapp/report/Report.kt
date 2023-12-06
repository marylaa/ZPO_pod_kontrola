package com.example.myapp.report

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import java.text.SimpleDateFormat
import java.util.*

data class Report(
    var valuesList: Array<Value> = emptyArray<Value>(),
    internal var mood: String? = "",
    internal var notes: String? = ""
){
    internal var date: String = ""
    var user: String = ""

    constructor() : this(emptyArray<Value>(), "", ""
    )

    init {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        date = sdf.format(c.time)
    }

    init {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        this.user = uid ?: ""
    }

    public fun setMood(mood: String?) {
        this.mood = mood
    }

    public fun setNotes(notes: String?) {
        this.notes = notes
    }

    public fun setValueList(array: Array<Value>){
        this.valuesList = array
    }
    public fun printReport(): String{
        return this.mood + " " + this.notes + " "
    }

    public fun getMood(): String{
        return mood.toString()
    }

    public fun getNotes(): String? {
        return notes
    }


    private lateinit var firestore : FirebaseFirestore

    init{
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    fun saveToFirebase() {
        val dbFirebase = FirebaseDatabase.getInstance()
        val dbReference = dbFirebase.getReference()


        val user = FirebaseAuth.getInstance().currentUser;
        val uid = user?.uid

        println("value list" + valuesList[0].getInputAsString())


        dbReference.child("report").push().setValue(
            mapOf(
                "Ciśnienie" to this.valuesList[0].getInputAsString(),
                "Aktywność" to this.valuesList[1].getInputAsString(),
                "Waga" to this.valuesList[2].getInputAsString(),
                "Sen" to this.valuesList[3].getInputAsString(),
                "Temp" to this.valuesList[4].getInputAsString(),
                "Poziom" to this.valuesList[5].getInputAsString(),
                "mood" to this.mood,
                "notes" to this.notes,
                "date" to this.date,
                "user" to this.user
            ),
            uid
        )
    }
}