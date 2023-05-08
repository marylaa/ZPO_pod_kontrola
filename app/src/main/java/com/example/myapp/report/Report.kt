package com.example.myapp.report

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import java.text.SimpleDateFormat
import java.util.*


//@Serializable
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
//        val c = Calendar.getInstance()
//        val year = c.get(Calendar.YEAR)
//        val month = c.get(Calendar.MONTH) + 1
//        val day = c.get(Calendar.DAY_OF_MONTH)
//
//        date = "$year-$month-$day"

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

        val user = FirebaseAuth.getInstance().currentUser;
        val uid = user?.uid

//        dbReference.child("report").push().setValue(this)
        dbReference.child("report").push().setValue(
            mapOf(
//                "ValuesList" to this.valuesList.joinToString(","),
                "Ciśnienie" to this.valuesList[0].getInput().toString(),
                "Aktywność" to this.valuesList[1].getInput().toString(),
                "Waga" to this.valuesList[2].getInput().toString(),
                "Sen" to this.valuesList[3].getInput().toString(),
                "Temp" to this.valuesList[4].getInput().toString(),
                "Poziom" to this.valuesList[5].getInput().toString(),
                "mood" to this.mood,
                "notes" to this.notes,
                "date" to this.date,
                "user" to this.user
            ),
            uid
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