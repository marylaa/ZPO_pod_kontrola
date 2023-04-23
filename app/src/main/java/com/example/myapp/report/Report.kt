package com.example.myapp.report

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import java.time.LocalDate
import java.util.*


//@Serializable
class Report(
    private var ValuesList: Array<Value>,
    private var mood: String?,
    private var notes: String,
){
    private var date: String = ""
//    private val date: LocalDate

//@Contextual
//var date: LocalDate? = null
//
//    init {
//        date = LocalDate.now()
//    }

//    constructor(ValuesList: String, mood: String, notes: String) : this() {
//        this.ValuesList = ValuesList.split(",").map { Value(it) }.toTypedArray()
//        this.mood = mood
//        this.notes = notes
//    }



//    fun setArray(array: Array<Value>) {
//        this.ValuesList = array
//    }

//    val c = Calendar.getInstance()
//
//    val year = c.get(Calendar.YEAR)
//    val month = c.get(Calendar.MONTH)
//    val day = c.get(Calendar.DAY_OF_MONTH)
//
//    val hour = c.get(Calendar.HOUR_OF_DAY)
//    val minute = c.get(Calendar.MINUTE)

    init {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)

        date = "$year-$month-$day"
    }

    fun setMood(mood: String) {
        this.mood = mood
    }

    fun setNotes(notes: String) {
        this.notes = notes
    }

    fun printReport(): String{
        return this.mood + " " + this.notes + " "
    }

    fun getMood(): String{
        return mood.toString()
    }

    fun getNotes(): String {
        return notes
    }


//    fun toJson() {
//        val json = Json.encodeToString(this)
//    }

    private lateinit var firestore : FirebaseFirestore

    init{
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }



    fun saveToFirebase() {

////
//        val valuesList = this.ValuesList.toString()
//        val mood = this.mood.toString()
//        val notes = this.notes.toString()
//
//////
//////
//        val report = Report(valuesList,mood,notes)
////
//////        val json = Json.encodeToString(this)
////


        val dbFirebase = FirebaseDatabase.getInstance()
        val dbReference = dbFirebase.getReference()

//        dbReference.child("report").push().setValue(this)
        dbReference.child("report").push().setValue(
            mapOf(
                "ValuesList" to this.ValuesList.joinToString(","),
                "mood" to this.mood,
                "notes" to this.notes,
                "date" to this.date
            )
        )



//        val document = firestore.collection("report").document()
//        val handle = document.set(this)
//        handle.addOnSuccessListener { Log.d("git", "gir") }

    }





//    override fun toString(): String {
//        return "Report(ValuesList=${ValuesList.contentToString()}, mood=$mood, notes='$notes', date=$date)"
//    }


}

//import com.example.test.Value
//import com.google.firebase.firestore.PropertyName
//import java.time.LocalDate
//
//class Report(
//    @get:PropertyName("valuesList") @set:PropertyName("valuesList") private var valuesList: Array<Value>,
//    @get:PropertyName("mood") @set:PropertyName("mood") private var mood: String?,
//    @get:PropertyName("notes") @set:PropertyName("notes") private var notes: String
//) {
//    private val date: LocalDate
//
//    init {
//        date = LocalDate.now()
//    }
//
//    fun setArray(array: Array<Value>) {
//        this.valuesList = array
//    }
//
//    fun setMood(mood: String) {
//        this.mood = mood
//    }
//
//    fun setNotes(notes: String) {
//        this.notes = notes
//    }
//
//    fun printReport(): String{
//        return this.mood + " " + this.notes + " " + this.date
//    }
//}