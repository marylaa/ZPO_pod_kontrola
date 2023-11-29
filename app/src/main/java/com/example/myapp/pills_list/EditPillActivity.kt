package com.example.myapp.pills_list

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.myapp.settings.PatientSettingsActivity
import com.example.myapp.R
import com.example.myapp.login.BaseActivity
import com.example.myapp.monthly_report.MainActivityMonthlyReport
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class EditPillActivity : BaseActivity(), View.OnClickListener {

    private var pillId: String? = null
    private var pill: PillModel? = null
    private var pillCustom: PillModelCustom? = null
    private var saveButton: Button? = null
    private lateinit var pillName: AutoCompleteTextView
    private var inputLeft: EditText? = null
    private var inputPackage: EditText? = null
    private var selectedFrequency: String = ""

    private lateinit var dbRef: DatabaseReference
    private lateinit var inputHour1: EditText
    private lateinit var inputMinute1: EditText
    private lateinit var inputHour2: EditText
    private lateinit var inputMinute2: EditText
    private lateinit var inputHour3: EditText
    private lateinit var inputMinute3: EditText

    private lateinit var hours: Array<Int?>
    private lateinit var minutes: Array<Int?>
    private var amountLeft: Int? = 0
    private var amountInBox: Int? = 0

    private lateinit var hoursMonday: Array<Int?>
    private lateinit var minutesMonday: Array<Int?>
    private lateinit var hoursTuesday: Array<Int?>
    private lateinit var minutesTuesday: Array<Int?>
    private lateinit var hoursWednesday: Array<Int?>
    private lateinit var minutesWednesday: Array<Int?>
    private lateinit var hoursThursday: Array<Int?>
    private lateinit var minutesThursday: Array<Int?>
    private lateinit var hoursFriday: Array<Int?>
    private lateinit var minutesFriday: Array<Int?>
    private lateinit var hoursSaturday: Array<Int?>
    private lateinit var minutesSaturday: Array<Int?>
    private lateinit var hoursSunday: Array<Int?>
    private lateinit var minutesSunday: Array<Int?>

    //NIESTANDARDOWA
    private lateinit var niestandardowaButtons: LinearLayout
    private lateinit var niestandardowaSeparator1: View
    private lateinit var niestandardowaSeparator2: View
    private lateinit var text1: TextView
    private lateinit var text11: TextView
    private lateinit var mondayButton: Button
    private lateinit var tuesdayButton: Button
    private lateinit var wednesdayButton: Button
    private lateinit var thursdayButton: Button
    private lateinit var fridayButton: Button
    private lateinit var saturdayButton: Button
    private lateinit var sundayButton: Button
    private lateinit var mondayButtonRemove: ImageButton
    private lateinit var tuesdayButtonRemove: ImageButton
    private lateinit var wednesdayButtonRemove: ImageButton
    private lateinit var thursdayButtonRemove: ImageButton
    private lateinit var fridayButtonRemove: ImageButton
    private lateinit var saturdayButtonRemove: ImageButton
    private lateinit var sundayButtonRemove: ImageButton
    private var monday = 0
    private var tuesday = 0
    private var wednesday = 0
    private var thursday = 0
    private var friday = 0
    private var saturday = 0
    private var sunday = 0

    //MONDAY
    private lateinit var mondayText: TextView
    private lateinit var mondayTextViewHour1: TextView
    private lateinit var mondayTextViewHour2: TextView
    private lateinit var mondayTextViewHour3: TextView
    private lateinit var mondayTextView1: TextView
    private lateinit var mondayTextView2: TextView
    private lateinit var mondayTextView3: TextView
    private lateinit var mondayInputHour1: EditText
    private lateinit var mondayInputMinute1: EditText
    private lateinit var mondayInputHour2: EditText
    private lateinit var mondayInputMinute2: EditText
    private lateinit var mondayInputHour3: EditText
    private lateinit var mondayInputMinute3: EditText

    //TUESDAY
    private lateinit var tuesdayText: TextView
    private lateinit var tuesdayTextViewHour1: TextView
    private lateinit var tuesdayTextViewHour2: TextView
    private lateinit var tuesdayTextViewHour3: TextView
    private lateinit var tuesdayTextView1: TextView
    private lateinit var tuesdayTextView2: TextView
    private lateinit var tuesdayTextView3: TextView
    private lateinit var tuesdayInputHour1: EditText
    private lateinit var tuesdayInputMinute1: EditText
    private lateinit var tuesdayInputHour2: EditText
    private lateinit var tuesdayInputMinute2: EditText
    private lateinit var tuesdayInputHour3: EditText
    private lateinit var tuesdayInputMinute3: EditText

    //WEDNESDAY
    private lateinit var wednesdayText: TextView
    private lateinit var wednesdayTextViewHour1: TextView
    private lateinit var wednesdayTextViewHour2: TextView
    private lateinit var wednesdayTextViewHour3: TextView
    private lateinit var wednesdayTextView1: TextView
    private lateinit var wednesdayTextView2: TextView
    private lateinit var wednesdayTextView3: TextView
    private lateinit var wednesdayInputHour1: EditText
    private lateinit var wednesdayInputMinute1: EditText
    private lateinit var wednesdayInputHour2: EditText
    private lateinit var wednesdayInputMinute2: EditText
    private lateinit var wednesdayInputHour3: EditText
    private lateinit var wednesdayInputMinute3: EditText

    //THURSDAY
    private lateinit var thursdayText: TextView
    private lateinit var thursdayTextViewHour1: TextView
    private lateinit var thursdayTextViewHour2: TextView
    private lateinit var thursdayTextViewHour3: TextView
    private lateinit var thursdayTextView1: TextView
    private lateinit var thursdayTextView2: TextView
    private lateinit var thursdayTextView3: TextView
    private lateinit var thursdayInputHour1: EditText
    private lateinit var thursdayInputMinute1: EditText
    private lateinit var thursdayInputHour2: EditText
    private lateinit var thursdayInputMinute2: EditText
    private lateinit var thursdayInputHour3: EditText
    private lateinit var thursdayInputMinute3: EditText

    //FRIDAY
    private lateinit var fridayText: TextView
    private lateinit var fridayTextViewHour1: TextView
    private lateinit var fridayTextViewHour2: TextView
    private lateinit var fridayTextViewHour3: TextView
    private lateinit var fridayTextView1: TextView
    private lateinit var fridayTextView2: TextView
    private lateinit var fridayTextView3: TextView
    private lateinit var fridayInputHour1: EditText
    private lateinit var fridayInputMinute1: EditText
    private lateinit var fridayInputHour2: EditText
    private lateinit var fridayInputMinute2: EditText
    private lateinit var fridayInputHour3: EditText
    private lateinit var fridayInputMinute3: EditText

    //SATURDAY
    private lateinit var saturdayText: TextView
    private lateinit var saturdayTextViewHour1: TextView
    private lateinit var saturdayTextViewHour2: TextView
    private lateinit var saturdayTextViewHour3: TextView
    private lateinit var saturdayTextView1: TextView
    private lateinit var saturdayTextView2: TextView
    private lateinit var saturdayTextView3: TextView
    private lateinit var saturdayInputHour1: EditText
    private lateinit var saturdayInputMinute1: EditText
    private lateinit var saturdayInputHour2: EditText
    private lateinit var saturdayInputMinute2: EditText
    private lateinit var saturdayInputHour3: EditText
    private lateinit var saturdayInputMinute3: EditText

    //SUNDAY
    private lateinit var sundayText: TextView
    private lateinit var sundayTextViewHour1: TextView
    private lateinit var sundayTextViewHour2: TextView
    private lateinit var sundayTextViewHour3: TextView
    private lateinit var sundayTextView1: TextView
    private lateinit var sundayTextView2: TextView
    private lateinit var sundayTextView3: TextView
    private lateinit var sundayInputHour1: EditText
    private lateinit var sundayInputMinute1: EditText
    private lateinit var sundayInputHour2: EditText
    private lateinit var sundayInputMinute2: EditText
    private lateinit var sundayInputHour3: EditText
    private lateinit var sundayInputMinute3: EditText

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pill_patient)

        val backButton = findViewById<ImageButton>(R.id.close)
        backButton.setOnClickListener(this)

        saveButton = findViewById(R.id.savePill)
        inputLeft = findViewById(R.id.amountLeft)
        inputPackage = findViewById(R.id.inBox)
        inputHour1 = findViewById(R.id.hourTime1)
        inputMinute1 = findViewById(R.id.minuteTime1)
        inputHour2 = findViewById(R.id.hourTime2)
        inputMinute2 = findViewById(R.id.minuteTime2)
        inputHour3 = findViewById(R.id.hourTime3)
        inputMinute3 = findViewById(R.id.minuteTime3)
        val text2 = findViewById<TextView>(R.id.textViewHour2)
        val text22 = findViewById<TextView>(R.id.textView2)
        val text3 = findViewById<TextView>(R.id.textViewHour3)
        val text33 = findViewById<TextView>(R.id.textView3)

        text1 = findViewById(R.id.textViewHour1)
        text11 = findViewById(R.id.textView1)
        inputHour1 = findViewById(R.id.hourTime1)
        inputMinute1 = findViewById(R.id.minuteTime1)
        inputHour2 = findViewById(R.id.hourTime2)
        inputMinute2 = findViewById(R.id.minuteTime2)
        inputHour3 = findViewById(R.id.hourTime3)
        inputMinute3 = findViewById(R.id.minuteTime3)
        defineNiestandardowa()

        //////////////////////////////////////////////////////////////////////////////////////
        pillName = findViewById(R.id.pillName)
        val adapter2 = ArrayAdapter<String>(
            this, android.R.layout.simple_dropdown_item_1line, ArrayList<String>())
        pillName.setAdapter(adapter2)

        pillName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val userInput = charSequence.toString()
                if (!TextUtils.isEmpty(userInput)) {
                    updateSuggestions()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })


        //////////////////////////////////////////////////////////////////////////////////////


        saveButton?.setOnClickListener{
            if (validatePillDetails()) {
                savePill { success ->
                    if (success) {
                        runOnUiThread {
                            Toast.makeText(this@EditPillActivity, "Tabletka została edytowana", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@EditPillActivity, "Wystąpił błąd", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }



        pillId = intent.getStringExtra("pillId")
        dbRef = FirebaseDatabase.getInstance().getReference("Pills")

        dbRef.child(pillId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val frequencyValue = snapshot.child("frequency").getValue(String::class.java)

                if (frequencyValue != null && frequencyValue != "Niestandardowa") {
                    // To jest PillModel
                    pill = snapshot.getValue(PillModel::class.java)

                    pill?.let {
                        pillName?.setText(it.name)
                        inputLeft?.setText(it.availability.toString())
                        inputPackage?.setText(it.inBox.toString())
                        val hour1 = it.time_list?.getOrNull(0)?.getOrNull(0)?.toString()?.split(":")
                        inputHour1?.setText(hour1?.getOrNull(0))
                        inputMinute1?.setText(hour1?.getOrNull(1))

                        val spinner = findViewById<Spinner>(R.id.spinner1)
                        when (it.frequency) {
                            "Codziennie" -> spinner.setSelection(0)
                            "Dwa razy dziennie" -> {
                                spinner.setSelection(1)
                                inputHour2.setVisibility(View.VISIBLE);
                                inputMinute2.setVisibility(View.VISIBLE);
                                text2.setVisibility(View.VISIBLE);
                                text22.setVisibility(View.VISIBLE);

                                val hour2 = it.time_list?.getOrNull(1)?.getOrNull(0)?.toString()?.split(":")
                                inputHour2?.setText(hour2?.getOrNull(0))
                                inputMinute2?.setText(hour2?.getOrNull(1))
                            }
                            "Trzy razy dziennie" -> {
                                spinner.setSelection(2)

                                inputHour2.setVisibility(View.VISIBLE);
                                inputMinute2.setVisibility(View.VISIBLE);
                                text2.setVisibility(View.VISIBLE);
                                text22.setVisibility(View.VISIBLE);
                                inputHour3.setVisibility(View.VISIBLE);
                                inputMinute3.setVisibility(View.VISIBLE);
                                text3.setVisibility(View.VISIBLE);
                                text33.setVisibility(View.VISIBLE);

                                val hour2 = it.time_list?.getOrNull(1)?.getOrNull(0)?.toString()?.split(":")
                                inputHour2?.setText(hour2?.getOrNull(0))
                                inputMinute2?.setText(hour2?.getOrNull(1))

                                val hour3 = it.time_list?.getOrNull(2)?.getOrNull(0)?.toString()?.split(":")
                                inputHour3?.setText(hour3?.getOrNull(0))
                                inputMinute3?.setText(hour3?.getOrNull(1))
                            }
                            "Co drugi dzień" -> spinner.setSelection(3)
                            "Raz w tygodniu" -> spinner.setSelection(4)
                            else -> {}
                        }
                    }
                } else {
                    // To jest PillModelCustom
                    pillCustom = snapshot.getValue(PillModelCustom::class.java)
                    defineNiestandardowa()

                    pillCustom?.let {
                        pillName?.setText(it.name)
                        inputLeft?.setText(it.availability.toString())
                        inputPackage?.setText(it.inBox.toString())
//                        val hour1 = it.time_list?.getOrNull(0)?.toString()?.split(":")
//                        inputHour1?.setText(hour1?.getOrNull(0))
//                        inputMinute1?.setText(hour1?.getOrNull(1))

                        for (item in pillCustom?.time_list.orEmpty()) {
                            val dayValue = item["day"]
                            val length = (item["times"] as? List<*>)?.size ?: 0
                            val timesList = item["times"] as? List<List<Any>>

                            println("dayyy value" + dayValue)
                            println(length)
                            if (dayValue!!.equals( "Poniedziałek")) {
                                var i = 0
                                while (i < length-1) {
                                    monday = i
                                    addMonday()
                                    i++
                                }
                                if(length == 1){
                                    val timeString = timesList!![0][0].toString()
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    mondayInputHour1.setText(hours.toString())
                                    mondayInputMinute1.setText(minutes.toString())

                                }else if(length == 2){
                                    val timeString = timesList!![0][0].toString()
                                    println(timeString)
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    mondayInputHour1.setText(hours.toString())
                                    mondayInputMinute1.setText(minutes.toString())
                                    val timeString2 = timesList!![1][0].toString()
                                    println(timeString2)
                                    val timeParts2 = timeString2.split(":")
                                    val hours2 = timeParts2[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes2 = timeParts2[1].toInt() // Konwersja na liczbę całkowitą
                                    mondayInputHour2.setText(hours2.toString())
                                    mondayInputMinute2.setText(minutes2.toString())

                                }else{
                                    val timeString = timesList!![0][0].toString()
                                    println(timeString)
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    mondayInputHour1.setText(hours.toString())
                                    mondayInputMinute1.setText(minutes.toString())
                                    val timeString2 = timesList!![1][0].toString()
                                    println(timeString2)
                                    val timeParts2 = timeString2.split(":")
                                    val hours2 = timeParts2[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes2 = timeParts2[1].toInt() // Konwersja na liczbę całkowitą
                                    mondayInputHour2.setText(hours2.toString())
                                    mondayInputMinute2.setText(minutes2.toString())
                                    val timeString3 = timesList!![2][0].toString()
                                    println(timeString3)
                                    val timeParts3 = timeString3.split(":")
                                    val hours3 = timeParts3[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes3 = timeParts3[1].toInt() // Konwersja na liczbę całkowitą
                                    mondayInputHour3.setText(hours3.toString())
                                    mondayInputMinute3.setText(minutes3.toString())

                                }
                            } else if (dayValue!!.equals( "Wtorek")) {
                                var i = 0
                                while (i < length-1) {
                                    tuesday = i
                                    addTuesday()
                                    i++
                                }
                                if(length == 1){
                                    val timeString = timesList!![0][0].toString()
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    tuesdayInputHour1.setText(hours.toString())
                                    tuesdayInputMinute1.setText(minutes.toString())

                                }else if(length == 2){
                                    val timeString = timesList!![0][0].toString()
                                    println(timeString)
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    tuesdayInputHour1.setText(hours.toString())
                                    tuesdayInputMinute1.setText(minutes.toString())
                                    val timeString2 = timesList!![1][0].toString()
                                    println(timeString2)
                                    val timeParts2 = timeString2.split(":")
                                    val hours2 = timeParts2[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes2 = timeParts2[1].toInt() // Konwersja na liczbę całkowitą
                                    tuesdayInputHour2.setText(hours2.toString())
                                    tuesdayInputMinute2.setText(minutes2.toString())

                                }else{
                                    val timeString = timesList!![0][0].toString()
                                    println(timeString)
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    tuesdayInputHour1.setText(hours.toString())
                                    tuesdayInputMinute1.setText(minutes.toString())
                                    val timeString2 = timesList!![1][0].toString()
                                    println(timeString2)
                                    val timeParts2 = timeString2.split(":")
                                    val hours2 = timeParts2[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes2 = timeParts2[1].toInt() // Konwersja na liczbę całkowitą
                                    tuesdayInputHour2.setText(hours2.toString())
                                    tuesdayInputMinute2.setText(minutes2.toString())
                                    val timeString3 = timesList!![2][0].toString()
                                    println(timeString3)
                                    val timeParts3 = timeString3.split(":")
                                    val hours3 = timeParts3[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes3 = timeParts3[1].toInt() // Konwersja na liczbę całkowitą
                                    tuesdayInputHour3.setText(hours3.toString())
                                    tuesdayInputMinute3.setText(minutes3.toString())

                                }
                            } else if (dayValue!!.equals( "Środa")) {
                                var i = 0
                                while (i < length-1) {
                                    wednesday = i
                                    addWednesday()
                                    i++
                                }
                                if(length == 1){
                                    val timeString = timesList!![0][0].toString()
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    wednesdayInputHour1.setText(hours.toString())
                                    wednesdayInputMinute1.setText(minutes.toString())

                                }else if(length == 2){
                                    val timeString = timesList!![0][0].toString()
                                    println(timeString)
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    wednesdayInputHour1.setText(hours.toString())
                                    wednesdayInputMinute1.setText(minutes.toString())
                                    val timeString2 = timesList!![1][0].toString()
                                    println(timeString2)
                                    val timeParts2 = timeString2.split(":")
                                    val hours2 = timeParts2[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes2 = timeParts2[1].toInt() // Konwersja na liczbę całkowitą
                                    wednesdayInputHour2.setText(hours2.toString())
                                    wednesdayInputMinute2.setText(minutes2.toString())

                                }else{
                                    val timeString = timesList!![0][0].toString()
                                    println(timeString)
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    wednesdayInputHour1.setText(hours.toString())
                                    wednesdayInputMinute1.setText(minutes.toString())
                                    val timeString2 = timesList!![1][0].toString()
                                    println(timeString2)
                                    val timeParts2 = timeString2.split(":")
                                    val hours2 = timeParts2[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes2 = timeParts2[1].toInt() // Konwersja na liczbę całkowitą
                                    wednesdayInputHour2.setText(hours2.toString())
                                    wednesdayInputMinute2.setText(minutes2.toString())
                                    val timeString3 = timesList!![2][0].toString()
                                    println(timeString3)
                                    val timeParts3 = timeString3.split(":")
                                    val hours3 = timeParts3[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes3 = timeParts3[1].toInt() // Konwersja na liczbę całkowitą
                                    wednesdayInputHour3.setText(hours3.toString())
                                    wednesdayInputMinute3.setText(minutes3.toString())

                                }
                            } else if (dayValue!!.equals( "Czwartek")) {
                                var i = 0
                                while (i < length-1) {
                                    thursday = i
                                    addThursday()
                                    i++
                                }
                                if(length == 1){
                                    val timeString = timesList!![0][0].toString()
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    thursdayInputHour1.setText(hours.toString())
                                    thursdayInputMinute1.setText(minutes.toString())

                                }else if(length == 2){
                                    val timeString = timesList!![0][0].toString()
                                    println(timeString)
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    thursdayInputHour1.setText(hours.toString())
                                    thursdayInputMinute1.setText(minutes.toString())
                                    val timeString2 = timesList!![1][0].toString()
                                    println(timeString2)
                                    val timeParts2 = timeString2.split(":")
                                    val hours2 = timeParts2[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes2 = timeParts2[1].toInt() // Konwersja na liczbę całkowitą
                                    thursdayInputHour2.setText(hours2.toString())
                                    thursdayInputMinute2.setText(minutes2.toString())

                                }else{
                                    val timeString = timesList!![0][0].toString()
                                    println(timeString)
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    thursdayInputHour1.setText(hours.toString())
                                    thursdayInputMinute1.setText(minutes.toString())
                                    val timeString2 = timesList!![1][0].toString()
                                    println(timeString2)
                                    val timeParts2 = timeString2.split(":")
                                    val hours2 = timeParts2[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes2 = timeParts2[1].toInt() // Konwersja na liczbę całkowitą
                                    thursdayInputHour2.setText(hours2.toString())
                                    thursdayInputMinute2.setText(minutes2.toString())
                                    val timeString3 = timesList!![2][0].toString()
                                    println(timeString3)
                                    val timeParts3 = timeString3.split(":")
                                    val hours3 = timeParts3[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes3 = timeParts3[1].toInt() // Konwersja na liczbę całkowitą
                                    thursdayInputHour3.setText(hours3.toString())
                                    thursdayInputMinute3.setText(minutes3.toString())

                                }
                            } else if (dayValue!!.equals( "Piątek")) {
                                var i = 0
                                while (i < length-1) {
                                    friday = i
                                    addFriday()
                                    i++
                                }
                                if(length == 1){
                                    val timeString = timesList!![0][0].toString()
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    fridayInputHour1.setText(hours.toString())
                                    fridayInputMinute1.setText(minutes.toString())

                                }else if(length == 2){
                                    val timeString = timesList!![0][0].toString()
                                    println(timeString)
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    fridayInputHour1.setText(hours.toString())
                                    fridayInputMinute1.setText(minutes.toString())
                                    val timeString2 = timesList!![1][0].toString()
                                    println(timeString2)
                                    val timeParts2 = timeString2.split(":")
                                    val hours2 = timeParts2[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes2 = timeParts2[1].toInt() // Konwersja na liczbę całkowitą
                                    fridayInputHour2.setText(hours2.toString())
                                    fridayInputMinute2.setText(minutes2.toString())

                                }else{
                                    val timeString = timesList!![0][0].toString()
                                    println(timeString)
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    fridayInputHour1.setText(hours.toString())
                                    fridayInputMinute1.setText(minutes.toString())
                                    val timeString2 = timesList!![1][0].toString()
                                    println(timeString2)
                                    val timeParts2 = timeString2.split(":")
                                    val hours2 = timeParts2[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes2 = timeParts2[1].toInt() // Konwersja na liczbę całkowitą
                                    fridayInputHour2.setText(hours2.toString())
                                    fridayInputMinute2.setText(minutes2.toString())
                                    val timeString3 = timesList!![2][0].toString()
                                    println(timeString3)
                                    val timeParts3 = timeString3.split(":")
                                    val hours3 = timeParts3[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes3 = timeParts3[1].toInt() // Konwersja na liczbę całkowitą
                                    fridayInputHour3.setText(hours3.toString())
                                    fridayInputMinute3.setText(minutes3.toString())

                                }
                            } else if (dayValue!!.equals( "Sobota")) {
                                var i = 0
                                while (i < length-1) {
                                    saturday = i
                                    addSaturday()
                                    i++
                                }
                                if(length == 1){
                                    val timeString = timesList!![0][0].toString()
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    saturdayInputHour1.setText(hours.toString())
                                    saturdayInputMinute1.setText(minutes.toString())

                                }else if(length == 2){
                                    val timeString = timesList!![0][0].toString()
                                    println(timeString)
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    saturdayInputHour1.setText(hours.toString())
                                    saturdayInputMinute1.setText(minutes.toString())
                                    val timeString2 = timesList!![1][0].toString()
                                    println(timeString2)
                                    val timeParts2 = timeString2.split(":")
                                    val hours2 = timeParts2[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes2 = timeParts2[1].toInt() // Konwersja na liczbę całkowitą
                                    saturdayInputHour2.setText(hours2.toString())
                                    saturdayInputMinute2.setText(minutes2.toString())

                                }else{
                                    val timeString = timesList!![0][0].toString()
                                    println(timeString)
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    saturdayInputHour1.setText(hours.toString())
                                    saturdayInputMinute1.setText(minutes.toString())
                                    val timeString2 = timesList!![1][0].toString()
                                    println(timeString2)
                                    val timeParts2 = timeString2.split(":")
                                    val hours2 = timeParts2[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes2 = timeParts2[1].toInt() // Konwersja na liczbę całkowitą
                                    saturdayInputHour2.setText(hours2.toString())
                                    saturdayInputMinute2.setText(minutes2.toString())
                                    val timeString3 = timesList!![2][0].toString()
                                    println(timeString3)
                                    val timeParts3 = timeString3.split(":")
                                    val hours3 = timeParts3[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes3 = timeParts3[1].toInt() // Konwersja na liczbę całkowitą
                                    saturdayInputHour3.setText(hours3.toString())
                                    saturdayInputMinute3.setText(minutes3.toString())

                                }
                            } else if (dayValue!!.equals( "Niedziela")) {
                                var i = 0
                                while (i < length-1) {
                                    sunday = i
                                    addSunday()
                                    i++
                                }
                                if(length == 1){
                                    val timeString = timesList!![0][0].toString()
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    sundayInputHour1.setText(hours.toString())
                                    sundayInputMinute1.setText(minutes.toString())

                                }else if(length == 2){
                                    val timeString = timesList!![0][0].toString()
                                    println(timeString)
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    sundayInputHour1.setText(hours.toString())
                                    sundayInputMinute1.setText(minutes.toString())
                                    val timeString2 = timesList!![1][0].toString()
                                    println(timeString2)
                                    val timeParts2 = timeString2.split(":")
                                    val hours2 = timeParts2[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes2 = timeParts2[1].toInt() // Konwersja na liczbę całkowitą
                                    sundayInputHour2.setText(hours2.toString())
                                    sundayInputMinute2.setText(minutes2.toString())

                                }else{
                                    val timeString = timesList!![0][0].toString()
                                    println(timeString)
                                    val timeParts = timeString.split(":")
                                    val hours = timeParts[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes = timeParts[1].toInt() // Konwersja na liczbę całkowitą
                                    sundayInputHour1.setText(hours.toString())
                                    sundayInputMinute1.setText(minutes.toString())
                                    val timeString2 = timesList!![1][0].toString()
                                    println(timeString2)
                                    val timeParts2 = timeString2.split(":")
                                    val hours2 = timeParts2[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes2 = timeParts2[1].toInt() // Konwersja na liczbę całkowitą
                                    sundayInputHour2.setText(hours2.toString())
                                    sundayInputMinute2.setText(minutes2.toString())
                                    val timeString3 = timesList!![2][0].toString()
                                    println(timeString3)
                                    val timeParts3 = timeString3.split(":")
                                    val hours3 = timeParts3[0].toInt() // Konwersja na liczbę całkowitą
                                    val minutes3 = timeParts3[1].toInt() // Konwersja na liczbę całkowitą
                                    sundayInputHour3.setText(hours3.toString())
                                    sundayInputMinute3.setText(minutes3.toString())

                                }
                            }
                        }

                        val spinner = findViewById<Spinner>(R.id.spinner1)
                        when (it.frequency) {
                            "Codziennie" -> spinner.setSelection(0)
                            "Dwa razy dziennie" -> {
                                spinner.setSelection(1)
                                inputHour2.setVisibility(View.VISIBLE);
                                inputMinute2.setVisibility(View.VISIBLE);
                                text2.setVisibility(View.VISIBLE);
                                text22.setVisibility(View.VISIBLE);

                                val hour2 = it.time_list?.getOrNull(1)?.toString()?.split(":")
                                inputHour2?.setText(hour2?.getOrNull(0))
                                inputMinute2?.setText(hour2?.getOrNull(1))
                            }
                            "Trzy razy dziennie" -> {
                                spinner.setSelection(2)

                                inputHour2.setVisibility(View.VISIBLE);
                                inputMinute2.setVisibility(View.VISIBLE);
                                text2.setVisibility(View.VISIBLE);
                                text22.setVisibility(View.VISIBLE);
                                inputHour3.setVisibility(View.VISIBLE);
                                inputMinute3.setVisibility(View.VISIBLE);
                                text3.setVisibility(View.VISIBLE);
                                text33.setVisibility(View.VISIBLE);

                                val hour2 = it.time_list?.getOrNull(1)?.toString()?.split(":")
                                inputHour2?.setText(hour2?.getOrNull(0))
                                inputMinute2?.setText(hour2?.getOrNull(1))

                                val hour3 = it.time_list?.getOrNull(2)?.toString()?.split(":")
                                inputHour3?.setText(hour3?.getOrNull(0))
                                inputMinute3?.setText(hour3?.getOrNull(1))
                            }
                            "Co drugi dzień" -> spinner.setSelection(3)
                            "Raz w tygodniu" -> spinner.setSelection(4)
                            "Niestandardowa" -> {
                                spinner.setSelection(5)
                                niestandardowaButtons.setVisibility(View.VISIBLE);
                                niestandardowaSeparator1.setVisibility(View.VISIBLE);
                                niestandardowaSeparator2.setVisibility(View.VISIBLE);

                                inputHour3.text = null
                                inputMinute3.text = null
                                inputHour2.text = null
                                inputMinute2.text = null
                                inputHour1.text = null
                                inputMinute1.text = null
                                inputHour1.setVisibility(View.GONE);
                                inputMinute1.setVisibility(View.GONE);
                                text1.setVisibility(View.GONE);
                                text11.setVisibility(View.GONE);
                                inputHour2.setVisibility(View.GONE);
                                inputMinute2.setVisibility(View.GONE);
                                text2.setVisibility(View.GONE);
                                text22.setVisibility(View.GONE);
                                inputHour3.setVisibility(View.GONE);
                                inputMinute3.setVisibility(View.GONE);
                                text3.setVisibility(View.GONE);
                                text33.setVisibility(View.GONE);
                            }

                            else -> {}
                        }
                    }


                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })


        val spinner = findViewById<Spinner>(R.id.spinner1)
        val elements = arrayOf(
            "Codziennie",
            "Dwa razy dziennie",
            "Trzy razy dziennie",
            "Co drugi dzień",
            "Raz w tygodniu",
            "Niestandardowa"
        )
        val adapter = ArrayAdapter(this, R.layout.list_item, elements)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedFrequency = parent.getItemAtPosition(position) as String
                if (selectedFrequency.equals("Niestandardowa")) {
                    niestandardowaButtons.setVisibility(View.VISIBLE);
                    niestandardowaSeparator1.setVisibility(View.VISIBLE);
                    niestandardowaSeparator2.setVisibility(View.VISIBLE);

                    pillCustom?.let {
                        pillName?.setText(it.name)
                        inputLeft?.setText(it.availability.toString())
                        inputPackage?.setText(it.inBox.toString())
//                        val hour1 = it.time_list?.getOrNull(0)?.toString()?.split(":")
//                        inputHour1?.setText(hour1?.getOrNull(0))
//                        inputMinute1?.setText(hour1?.getOrNull(1))

                        for (item in pillCustom?.time_list.orEmpty()) {
                            val dayValue = item["day"]
                            val length = (item["times"] as? List<*>)?.size ?: 0
                            println("dayyy value" + dayValue)
                            println(length)
                            if (dayValue!!.equals( "Poniedziałek")) {
                                var i = 0
                                while (i < length-1) {
                                    monday = i
                                    addMonday()
                                    i++
                                }
                            } else if (dayValue!!.equals( "Wtorek")) {
                                var i = 0
                                while (i < length-1) {
                                    tuesday = i
                                    addTuesday()
                                    i++
                                }
                            } else if (dayValue!!.equals( "Środa")) {
                                var i = 0
                                while (i < length-1) {
                                    wednesday = i
                                    addWednesday()
                                    i++
                                }
                            } else if (dayValue!!.equals( "Czwartek")) {
                                var i = 0
                                while (i < length-1) {
                                    thursday = i
                                    addThursday()
                                    i++
                                }
                            } else if (dayValue!!.equals( "Piątek")) {
                                var i = 0
                                while (i < length-1) {
                                    friday = i
                                    addFriday()
                                    i++
                                }
                            } else if (dayValue!!.equals( "Sobota")) {
                                var i = 0
                                while (i < length-1) {
                                    saturday = i
                                    addSaturday()
                                    i++
                                }
                            } else if (dayValue!!.equals( "Niedziela")) {
                                var i = 0
                                while (i < length-1) {
                                    sunday = i
                                    addSunday()
                                    i++
                                }
                            }
                        }
                    }

                    inputHour3.text = null
                    inputMinute3.text = null
                    inputHour2.text = null
                    inputMinute2.text = null
                    inputHour1.text = null
                    inputMinute1.text = null
                    inputHour1.setVisibility(View.GONE);
                    inputMinute1.setVisibility(View.GONE);
                    text1.setVisibility(View.GONE);
                    text11.setVisibility(View.GONE);
                    inputHour2.setVisibility(View.GONE);
                    inputMinute2.setVisibility(View.GONE);
                    text2.setVisibility(View.GONE);
                    text22.setVisibility(View.GONE);
                    inputHour3.setVisibility(View.GONE);
                    inputMinute3.setVisibility(View.GONE);
                    text3.setVisibility(View.GONE);
                    text33.setVisibility(View.GONE);

                } else if (selectedFrequency.equals("Dwa razy dziennie")) {
                    inputHour2.setVisibility(View.VISIBLE);
                    inputMinute2.setVisibility(View.VISIBLE);
                    text2.setVisibility(View.VISIBLE);
                    text22.setVisibility(View.VISIBLE);

                    inputHour3.setVisibility(View.GONE);
                    inputMinute3.setVisibility(View.GONE);
                    text3.setVisibility(View.GONE);
                    text33.setVisibility(View.GONE);
                    inputHour3.text = null
                    inputMinute3.text = null
                    closeNiestandardowaView()
                } else if (selectedFrequency.equals("Trzy razy dziennie")) {
                    inputHour2.setVisibility(View.VISIBLE);
                    inputMinute2.setVisibility(View.VISIBLE);
                    text2.setVisibility(View.VISIBLE);
                    text22.setVisibility(View.VISIBLE);
                    inputHour3.setVisibility(View.VISIBLE);
                    inputMinute3.setVisibility(View.VISIBLE);
                    text3.setVisibility(View.VISIBLE);
                    text33.setVisibility(View.VISIBLE);
                    closeNiestandardowaView()
                } else {
                    inputHour2.setVisibility(View.GONE);
                    inputHour2.text = null
                    inputMinute2.setVisibility(View.GONE);
                    inputMinute2.text = null
                    text2.setVisibility(View.GONE);
                    text22.setVisibility(View.GONE);

                    inputHour3.setVisibility(View.GONE);
                    inputHour3.text = null
                    inputMinute3.setVisibility(View.GONE);
                    inputMinute3.text = null
                    text3.setVisibility(View.GONE);
                    text33.setVisibility(View.GONE);
                    closeNiestandardowaView()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@EditPillActivity, UserScheduleActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_report -> {
                    val intent = Intent(this@EditPillActivity, MainActivityMonthlyReport::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_settings -> {
                    val intent = Intent(this@EditPillActivity, PatientSettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

    }

    //////////////////////////////////////////////////////////////////////////////////

    private fun sendHttpRequest(): List<String>? {
        val task = DownloadPillsTask(pillName.text.toString())
        val suggestions: List<String>? = task.execute().get()
        return suggestions
    }

    private fun updateSuggestions() {
        val suggestions = sendHttpRequest()
        val adapter = pillName.adapter as ArrayAdapter<String>
        adapter.clear()
        Log.d("SUGESTIE", suggestions.toString())
        adapter.addAll(suggestions!!)
        adapter.notifyDataSetChanged()
    }

/////////////////////////////////////////////////////////////////////////////////


    fun goToSchedule(view: View) {
        val intent = Intent(this, UserScheduleActivity::class.java)
        startActivity(intent)
        finish()
    }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun savePill(completion: (Boolean) -> Unit) {
        dbRef = FirebaseDatabase.getInstance().getReference("Pills")

        var time1 = ""
        var times1 = mutableListOf<Any?>(time1, false)
        var times: MutableList<MutableList<Any?>?>? = null


        val current = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = current.format(formatter)

        if(pill!=null) {

            pill!!.name = pillName?.text.toString().trim() { it <= ' ' }
            pill!!.availability = inputLeft?.text.toString().toIntOrNull()
            pill!!.inBox = inputPackage?.text.toString().toIntOrNull()

            time1 = timeToString(hours.get(0), minutes.get(0))
            times1 = mutableListOf<Any?>(time1, false)
        }else{
            pillCustom!!.name = pillName?.text.toString().trim() { it <= ' ' }
            pillCustom!!.availability = inputLeft?.text.toString().toIntOrNull()
            pillCustom!!.inBox = inputPackage?.text.toString().toIntOrNull()
        }


//        var timesNiestandardowe: MutableList<HashMap<String, Any>>? = null
//        var timesNiestandardowe: MutableList<Any?>? = null
        val timesNiestandardowe: MutableList<HashMap<String, Any>> = mutableListOf()



        if (selectedFrequency.equals("Dwa razy dziennie")) {
            val time2 = timeToString(hours.get(1), minutes.get(1))
            val times2 = mutableListOf<Any?>(time2, false)
            times = mutableListOf(times1, times2)
        } else if (selectedFrequency.equals("Trzy razy dziennie")) {
            val time2 = timeToString(hours.get(1), minutes.get(1))
            val time3 = timeToString(hours.get(2), minutes.get(2))
            val times2 = mutableListOf<Any?>(time2, false)
            val times3 = mutableListOf<Any?>(time3, false)
            times = mutableListOf(times1, times2, times3)
        } else if (selectedFrequency.equals("Niestandardowa")) {

            pillCustom!!.name = pillName?.text.toString().trim() { it <= ' ' }
            pillCustom!!.availability = inputLeft?.text.toString().toIntOrNull()
            pillCustom!!.inBox = inputPackage?.text.toString().toIntOrNull()
            data class DayObject(val day: String, val times: MutableList<MutableList<Any?>> = mutableListOf())

            val timesMonday: MutableList<MutableList<Any?>> = mutableListOf()
            for((index, timeMonday) in hoursMonday.withIndex()) {
                if (timeMonday !== null) {
                    timesMonday.add(mutableListOf<Any?>(timeToString(hoursMonday.get(index), minutesMonday.get(index)), false))
                }
            }
            val mondayList = DayObject("Poniedziałek", timesMonday)

            val timesTuesday: MutableList<MutableList<Any?>> = mutableListOf()
            for((index, timeTuesday) in hoursTuesday.withIndex()) {
                if (timeTuesday !== null) {
                    timesTuesday.add(mutableListOf<Any?>(timeToString(hoursTuesday.get(index), minutesTuesday.get(index)), false))
                }
            }
            val tuesdayList = DayObject("Wtorek", timesTuesday)

            val timesWednesday: MutableList<MutableList<Any?>> = mutableListOf()
            for((index, timeWednesday) in hoursWednesday.withIndex()) {
                if (timeWednesday !== null) {
                    timesWednesday.add(mutableListOf<Any?>(timeToString(hoursWednesday.get(index), minutesWednesday.get(index)), false))
                }
            }
            val wednesdayList = DayObject("Środa", timesWednesday)

            val timesThursday: MutableList<MutableList<Any?>> = mutableListOf()
            for((index, timeThursday) in hoursThursday.withIndex()) {
                if (timeThursday !== null) {
                    timesThursday.add(mutableListOf<Any?>(timeToString(hoursThursday.get(index), minutesThursday.get(index)), false))
                }
            }
            val thursdayList = DayObject("Czwartek", timesThursday)

            val timesFriday: MutableList<MutableList<Any?>> = mutableListOf()
            for((index, timeFriday) in hoursFriday.withIndex()) {
                if (timeFriday !== null) {
                    timesFriday.add(mutableListOf<Any?>(timeToString(hoursFriday.get(index), minutesFriday.get(index)), false))
                }
            }
            val fridayList = DayObject("Piątek", timesFriday)

            val timesSaturday: MutableList<MutableList<Any?>> = mutableListOf()
            for((index, timeSaturday) in hoursSaturday.withIndex()) {
                if (timeSaturday !== null) {
                    timesSaturday.add(mutableListOf<Any?>(timeToString(hoursSaturday.get(index), minutesSaturday.get(index)), false))
                }
            }
            val saturdayList = DayObject("Sobota", timesSaturday)

            val timesSunday: MutableList<MutableList<Any?>> = mutableListOf()
            for((index, timeSunday) in hoursSunday.withIndex()) {
                if (timeSunday !== null) {
                    timesSunday.add(mutableListOf<Any?>(timeToString(hoursSunday.get(index), minutesSunday.get(index)), false))
                }
            }
            val sundayList = DayObject("Niedziela", timesSunday)


//            timesNiestandardowe.addAll(
//                mutableListOf(mondayList, tuesdayList, wednesdayList, thursdayList, fridayList, saturdayList, sundayList)
//                    .filter { it.times.isNotEmpty() }
//                    .map { dayObject ->
//                        hashMapOf(
//                            "times" to dayObject.times,
//                            "day" to dayObject.day
//                        )
//                    }
//            )

            val daysList: List<DayObject> = listOf(
                mondayList, tuesdayList, wednesdayList, thursdayList, fridayList, saturdayList, sundayList
            ).filter { it.times.isNotEmpty() }


            for (dayObject in daysList) {
                val dayMap = hashMapOf<String, Any>()
                dayMap["times"] = dayObject.times
                dayMap["day"] = dayObject.day
                timesNiestandardowe.add(dayMap)
            }




        } else {
            times = mutableListOf(times1)
        }

        if(pillCustom != null){
            //jesli nie byly zmieniane czasy to data w bazie musi zostac ta sama
            if (!(pillCustom!!.frequency.equals(selectedFrequency))) {
                pillCustom!!.date_last = date

                if (selectedFrequency.equals("Niestandardowa")) {
                    // zostawienie checkboxa w stanie jaki byl
                    if (timesNiestandardowe != null) {
                        if (timesNiestandardowe.size < pillCustom!!.time_list!!.size) {
                            for (i in 0 until timesNiestandardowe.size) {
                                val pillCustomValue = pillCustom!!.time_list?.getOrNull(i)?.get("times")?.let {
                                    (it as? List<List<Any>>)?.getOrNull(0)?.getOrNull(1) as? Boolean
                                }

                                if (pillCustomValue == true) {
                                    timesNiestandardowe[i]?.set("1", true)
                                }
                            }

                        } else {
                            for (i in 0 until timesNiestandardowe.size) {
                                val pillCustomValue = pillCustom!!.time_list?.getOrNull(i)?.get("times")?.let {
                                    (it as? List<List<Any>>)?.getOrNull(0)?.getOrNull(1) as? Boolean
                                }

                                if (pillCustomValue == true) {
                                    timesNiestandardowe[i]?.set("1", true)
                                }
                            }

                        }
                    }

                    pillCustom!!.time_list = timesNiestandardowe
                    pillCustom!!.frequency = selectedFrequency

                    var nextDay = ""
                    if (selectedFrequency.equals("Co drugi dzień")) {
                        val next = current.plusDays(2)
                        nextDay = next.format(formatter)
                    } else if (selectedFrequency.equals("Raz w tygodniu")) {
                        val next = current.plusDays(7)
                        nextDay = next.format(formatter)
                    } else {
                        val next = current.plusDays(1)
                        nextDay = next.format(formatter)
                    }


                    pillCustom!!.date_next = nextDay

                }else {

                    if (selectedFrequency.equals("Niestandardowa")) {
                        // zostawienie checkboxa w stanie jaki byl
                        if (timesNiestandardowe != null) {
                            if (timesNiestandardowe.size < pillCustom!!.time_list!!.size) {
                                for (i in 0 until timesNiestandardowe.size) {
                                    val pillCustomValue = pillCustom!!.time_list?.getOrNull(i)?.get("times")?.let {
                                        (it as? List<List<Any>>)?.getOrNull(0)?.getOrNull(1) as? Boolean
                                    }

                                    if (pillCustomValue == true) {
                                        timesNiestandardowe[i]?.set("1", true)
                                    }
                                }

                            } else {
                                for (i in 0 until timesNiestandardowe.size) {
                                    val pillCustomValue = pillCustom!!.time_list?.getOrNull(i)?.get("times")?.let {
                                        (it as? List<List<Any>>)?.getOrNull(0)?.getOrNull(1) as? Boolean
                                    }

                                    if (pillCustomValue == true) {
                                        timesNiestandardowe[i]?.set("1", true)
                                    }
                                }

                            }
                        }

                        pillCustom!!.time_list = timesNiestandardowe
                        pillCustom!!.frequency = selectedFrequency

                        var nextDay = ""
                        if (selectedFrequency.equals("Co drugi dzień")) {
                            val next = current.plusDays(2)
                            nextDay = next.format(formatter)
                        } else if (selectedFrequency.equals("Raz w tygodniu")) {
                            val next = current.plusDays(7)
                            nextDay = next.format(formatter)
                        } else {
                            val next = current.plusDays(1)
                            nextDay = next.format(formatter)
                        }


                        pillCustom!!.date_next = nextDay
                    }

                }





            } else {

                if (selectedFrequency.equals("Niestandardowa")) {
                    var same = true
                    if (timesNiestandardowe != null) {
                        for (i in 0 until timesNiestandardowe.size) {
                            println(pillCustom!!.time_list.toString())
                            println(timesNiestandardowe.toString())

                            val pillCustomHour = pillCustom!!.time_list?.getOrNull(i)?.get("times")?.let {
                                (it as? List<List<Any>>)?.getOrNull(0)?.getOrNull(0) as? String
                            }

                            val niestandardoweHour = timesNiestandardowe[i]?.get("times")?.let {
                                (it as? List<List<Any>>)?.getOrNull(0)?.getOrNull(0) as? String
                            }

                            if (!(pillCustomHour.equals(niestandardoweHour))) {
                                same = false
                            }
                        }
                    }
                    if (!same) {
                        // zostawienie checkboxa w stanie jaki byl
                        if (timesNiestandardowe != null) {
                            for (i in 0 until timesNiestandardowe.size) {
                                val pillCustomValue = pillCustom!!.time_list?.getOrNull(i)?.get("times")?.let {
                                    (it as? List<List<Any>>)?.getOrNull(0)?.getOrNull(1) as? Boolean
                                }

                                if (pillCustomValue == true) {
                                    timesNiestandardowe[i]?.set("1", true)
                                }
                            }
                        }
                        pillCustom!!.time_list = timesNiestandardowe
                    }
                }
            }

        }
        if(pillCustom == null){
            //jesli nie byly zmieniane czasy to data w bazie musi zostac ta sama
            if (!(pill!!.frequency.equals(selectedFrequency))) {
                pill!!.date_last = date

                    // zostawienie checkboxa w stanie jaki byl
                if (times != null) {
                        if (times.size < pill!!.time_list!!.size) {
                            for (i in 0..times.size - 1) {
                                if (pill!!.time_list!![i]?.get(1) as Boolean) {
                                    times[i]?.set(1, true)
                                }
                            }
                        } else {
                            for (i in 0..pill!!.time_list!!.size - 1) {
                                if (pill!!.time_list!![i]?.get(1) as Boolean) {
                                    times[i]?.set(1, true)
                                }
                            }
                        }
                    }

                    pill!!.time_list = times
                    pill!!.frequency = selectedFrequency

                    var nextDay = ""
                    if (selectedFrequency.equals("Co drugi dzień")) {
                        val next = current.plusDays(2)
                        nextDay = next.format(formatter)
                    } else if (selectedFrequency.equals("Raz w tygodniu")) {
                        val next = current.plusDays(7)
                        nextDay = next.format(formatter)
                    } else {
                        val next = current.plusDays(1)
                        nextDay = next.format(formatter)
                    }


                    pill!!.date_next = nextDay

                }else {

                        // zostawienie checkboxa w stanie jaki byl
                        if (times != null) {
                            if (times.size < pill!!.time_list!!.size) {
                                for (i in 0..times.size - 1) {
                                    if (pill!!.time_list!![i]?.get(1) as Boolean) {
                                        times[i]?.set(1, true)
                                    }
                                }
                            } else {
                                for (i in 0..pill!!.time_list!!.size - 1) {
                                    if (pill!!.time_list!![i]?.get(1) as Boolean) {
                                        times[i]?.set(1, true)
                                    }
                                }
                            }
                        }

                        pill!!.time_list = times
                        pill!!.frequency = selectedFrequency

                        var nextDay = ""
                        if (selectedFrequency.equals("Co drugi dzień")) {
                            val next = current.plusDays(2)
                            nextDay = next.format(formatter)
                        } else if (selectedFrequency.equals("Raz w tygodniu")) {
                            val next = current.plusDays(7)
                            nextDay = next.format(formatter)
                        } else {
                            val next = current.plusDays(1)
                            nextDay = next.format(formatter)
                        }


                        pill!!.date_next = nextDay

                }





            } else {

                    var same = true
                    if (timesNiestandardowe != null) {
                        for (i in 0 until timesNiestandardowe.size) {
                            println(pillCustom!!.time_list.toString())
                            println(timesNiestandardowe.toString())

                            val pillCustomHour = pillCustom!!.time_list?.getOrNull(i)?.get("times")?.let {
                                (it as? List<List<Any>>)?.getOrNull(0)?.getOrNull(0) as? String
                            }

                            val niestandardoweHour = timesNiestandardowe[i]?.get("times")?.let {
                                (it as? List<List<Any>>)?.getOrNull(0)?.getOrNull(0) as? String
                            }

                            if (!(pillCustomHour.equals(niestandardoweHour))) {
                                same = false
                            }
                        }
                    }
                    if (!same) {
                        // zostawienie checkboxa w stanie jaki byl
                        if (timesNiestandardowe != null) {
                            for (i in 0 until timesNiestandardowe.size) {
                                val pillCustomValue = pillCustom!!.time_list?.getOrNull(i)?.get("times")?.let {
                                    (it as? List<List<Any>>)?.getOrNull(0)?.getOrNull(1) as? Boolean
                                }

                                if (pillCustomValue == true) {
                                    timesNiestandardowe[i]?.set("1", true)
                                }
                            }

                        }
                        pillCustom!!.time_list = timesNiestandardowe
                    }
            }



        println(pillCustom!!.time_list)
        println(timesNiestandardowe)


        if(pillCustom == null) {
            dbRef.child(pill!!.id.toString()).setValue(pill).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    completion(true) // Zwróć true w przypadku powodzenia
                } else {
                    completion(false) // Zwróć false w przypadku błędu
                }
            }
        }else{
            dbRef.child(pillCustom!!.id.toString()).setValue(pillCustom).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    completion(true) // Zwróć true w przypadku powodzenia
                } else {
                    completion(false) // Zwróć false w przypadku błędu
                }
            }
        }
    }


    override fun onClick(view: View?) {
        if(view !=null){
            when (view.id){

                R.id.close ->{
                    val intent = Intent(this, UserScheduleActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun timeToString(hour: Int?, minute: Int?): String {
        var hour_new = hour.toString()
        var minute_new = minute.toString()
        if (hour!! < 10) {
            hour_new = "0" + hour.toString()
        }
        if (minute!! < 10) {
            minute_new = "0" + minute.toString()
        }
        return hour_new + ":" + minute_new
    }


    private fun addMonday() {
        if (monday == 0) {
            monday = 1
            mondayButtonRemove.setVisibility(View.VISIBLE)
            mondayText.setVisibility(View.VISIBLE)
            mondayTextViewHour1.setVisibility(View.VISIBLE)
            mondayTextView1.setVisibility(View.VISIBLE)
            mondayInputHour1.setVisibility(View.VISIBLE)
            mondayInputMinute1.setVisibility(View.VISIBLE)
        } else if (monday == 1) {
            monday = 2
            mondayTextViewHour2.setVisibility(View.VISIBLE)
            mondayTextView2.setVisibility(View.VISIBLE)
            mondayInputHour2.setVisibility(View.VISIBLE)
            mondayInputMinute2.setVisibility(View.VISIBLE)
        } else if (monday == 2) {
            monday = 3
            mondayTextViewHour3.setVisibility(View.VISIBLE)
            mondayTextView3.setVisibility(View.VISIBLE)
            mondayInputHour3.setVisibility(View.VISIBLE)
            mondayInputMinute3.setVisibility(View.VISIBLE)
        } else {
            Toast.makeText(
                this@EditPillActivity,
                "Maksymalnie można dodać 3 dawki dziennie",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addTuesday() {
        if (tuesday == 0) {
            tuesday = 1
            tuesdayButtonRemove.setVisibility(View.VISIBLE)
            tuesdayText.setVisibility(View.VISIBLE)
            tuesdayTextViewHour1.setVisibility(View.VISIBLE)
            tuesdayTextView1.setVisibility(View.VISIBLE)
            tuesdayInputHour1.setVisibility(View.VISIBLE)
            tuesdayInputMinute1.setVisibility(View.VISIBLE)
        } else if (tuesday == 1) {
            tuesday = 2
            tuesdayTextViewHour2.setVisibility(View.VISIBLE)
            tuesdayTextView2.setVisibility(View.VISIBLE)
            tuesdayInputHour2.setVisibility(View.VISIBLE)
            tuesdayInputMinute2.setVisibility(View.VISIBLE)
        } else if (tuesday == 2) {
            tuesday = 3
            tuesdayTextViewHour3.setVisibility(View.VISIBLE)
            tuesdayTextView3.setVisibility(View.VISIBLE)
            tuesdayInputHour3.setVisibility(View.VISIBLE)
            tuesdayInputMinute3.setVisibility(View.VISIBLE)
        } else {
            Toast.makeText(
                this@EditPillActivity,
                "Maksymalnie można dodać 3 dawki dziennie",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addWednesday() {
        println("add wed")
        println(wednesday)
        if (wednesday == 0) {
            wednesday = 1
            wednesdayButtonRemove.setVisibility(View.VISIBLE)
            wednesdayText.setVisibility(View.VISIBLE)
            wednesdayTextViewHour1.setVisibility(View.VISIBLE)
            wednesdayTextView1.setVisibility(View.VISIBLE)
            wednesdayInputHour1.setVisibility(View.VISIBLE)
            wednesdayInputMinute1.setVisibility(View.VISIBLE)
        } else if (wednesday == 1) {
            wednesday = 2
            wednesdayTextViewHour2.setVisibility(View.VISIBLE)
            wednesdayTextView2.setVisibility(View.VISIBLE)
            wednesdayInputHour2.setVisibility(View.VISIBLE)
            wednesdayInputMinute2.setVisibility(View.VISIBLE)
        } else if (wednesday == 2) {
            wednesday = 3
            wednesdayTextViewHour3.setVisibility(View.VISIBLE)
            wednesdayTextView3.setVisibility(View.VISIBLE)
            wednesdayInputHour3.setVisibility(View.VISIBLE)
            wednesdayInputMinute3.setVisibility(View.VISIBLE)
        } else {
            Toast.makeText(
                this@EditPillActivity,
                "Maksymalnie można dodać 3 dawki dziennie",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addThursday() {
        if (thursday == 0) {
            thursday = 1
            thursdayButtonRemove.setVisibility(View.VISIBLE)
            thursdayText.setVisibility(View.VISIBLE)
            thursdayTextViewHour1.setVisibility(View.VISIBLE)
            thursdayTextView1.setVisibility(View.VISIBLE)
            thursdayInputHour1.setVisibility(View.VISIBLE)
            thursdayInputMinute1.setVisibility(View.VISIBLE)
        } else if (thursday == 1) {
            thursday = 2
            thursdayTextViewHour2.setVisibility(View.VISIBLE)
            thursdayTextView2.setVisibility(View.VISIBLE)
            thursdayInputHour2.setVisibility(View.VISIBLE)
            thursdayInputMinute2.setVisibility(View.VISIBLE)
        } else if (thursday == 2) {
            thursday = 3
            thursdayTextViewHour3.setVisibility(View.VISIBLE)
            thursdayTextView3.setVisibility(View.VISIBLE)
            thursdayInputHour3.setVisibility(View.VISIBLE)
            thursdayInputMinute3.setVisibility(View.VISIBLE)
        } else {
            Toast.makeText(
                this@EditPillActivity,
                "Maksymalnie można dodać 3 dawki dziennie",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addFriday() {
        if (friday == 0) {
            friday = 1
            fridayButtonRemove.setVisibility(View.VISIBLE)
            fridayText.setVisibility(View.VISIBLE)
            fridayTextViewHour1.setVisibility(View.VISIBLE)
            fridayTextView1.setVisibility(View.VISIBLE)
            fridayInputHour1.setVisibility(View.VISIBLE)
            fridayInputMinute1.setVisibility(View.VISIBLE)
        } else if (friday == 1) {
            friday = 2
            fridayTextViewHour2.setVisibility(View.VISIBLE)
            fridayTextView2.setVisibility(View.VISIBLE)
            fridayInputHour2.setVisibility(View.VISIBLE)
            fridayInputMinute2.setVisibility(View.VISIBLE)
        } else if (friday == 2) {
            friday = 3
            fridayTextViewHour3.setVisibility(View.VISIBLE)
            fridayTextView3.setVisibility(View.VISIBLE)
            fridayInputHour3.setVisibility(View.VISIBLE)
            fridayInputMinute3.setVisibility(View.VISIBLE)
        } else {
            Toast.makeText(
                this@EditPillActivity,
                "Maksymalnie można dodać 3 dawki dziennie",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addSaturday() {
        if (saturday == 0) {
            saturday = 1
            saturdayButtonRemove.setVisibility(View.VISIBLE)
            saturdayText.setVisibility(View.VISIBLE)
            saturdayTextViewHour1.setVisibility(View.VISIBLE)
            saturdayTextView1.setVisibility(View.VISIBLE)
            saturdayInputHour1.setVisibility(View.VISIBLE)
            saturdayInputMinute1.setVisibility(View.VISIBLE)
        } else if (saturday == 1) {
            saturday = 2
            saturdayTextViewHour2.setVisibility(View.VISIBLE)
            saturdayTextView2.setVisibility(View.VISIBLE)
            saturdayInputHour2.setVisibility(View.VISIBLE)
            saturdayInputMinute2.setVisibility(View.VISIBLE)
        } else if (saturday == 2) {
            saturday = 3
            saturdayTextViewHour3.setVisibility(View.VISIBLE)
            saturdayTextView3.setVisibility(View.VISIBLE)
            saturdayInputHour3.setVisibility(View.VISIBLE)
            saturdayInputMinute3.setVisibility(View.VISIBLE)
        } else {
            Toast.makeText(
                this@EditPillActivity,
                "Maksymalnie można dodać 3 dawki dziennie",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addSunday() {
        if (sunday == 0) {
            sunday = 1
            sundayButtonRemove.setVisibility(View.VISIBLE)
            sundayText.setVisibility(View.VISIBLE)
            sundayTextViewHour1.setVisibility(View.VISIBLE)
            sundayTextView1.setVisibility(View.VISIBLE)
            sundayInputHour1.setVisibility(View.VISIBLE)
            sundayInputMinute1.setVisibility(View.VISIBLE)
        } else if (sunday == 1) {
            sunday = 2
            sundayTextViewHour2.setVisibility(View.VISIBLE)
            sundayTextView2.setVisibility(View.VISIBLE)
            sundayInputHour2.setVisibility(View.VISIBLE)
            sundayInputMinute2.setVisibility(View.VISIBLE)
        } else if (sunday == 2) {
            sunday = 3
            sundayTextViewHour3.setVisibility(View.VISIBLE)
            sundayTextView3.setVisibility(View.VISIBLE)
            sundayInputHour3.setVisibility(View.VISIBLE)
            sundayInputMinute3.setVisibility(View.VISIBLE)
        } else {
            Toast.makeText(
                this@EditPillActivity,
                "Maksymalnie można dodać 3 dawki dziennie",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun removeMonday() {
        if (monday == 1) {
            monday = 0
            mondayButtonRemove.setVisibility(View.GONE)
            mondayText.setVisibility(View.GONE)
            mondayTextViewHour1.setVisibility(View.GONE)
            mondayTextView1.setVisibility(View.GONE)
            mondayInputHour1.setVisibility(View.GONE)
            mondayInputMinute1.setVisibility(View.GONE)
            mondayInputHour1.text = null
            mondayInputMinute1.text = null
        } else if (monday == 2) {
            monday = 1
            mondayTextViewHour2.setVisibility(View.GONE)
            mondayTextView2.setVisibility(View.GONE)
            mondayInputHour2.setVisibility(View.GONE)
            mondayInputMinute2.setVisibility(View.GONE)
            mondayInputHour2.text = null
            mondayInputMinute2.text = null
        } else if (monday == 3) {
            monday = 2
            mondayTextViewHour3.setVisibility(View.GONE)
            mondayTextView3.setVisibility(View.GONE)
            mondayInputHour3.setVisibility(View.GONE)
            mondayInputMinute3.setVisibility(View.GONE)
            mondayInputHour3.text = null
            mondayInputMinute3.text = null
        }
    }

    private fun removeTuesday() {
        if (tuesday == 1) {
            tuesday = 0
            tuesdayButtonRemove.setVisibility(View.GONE)
            tuesdayText.setVisibility(View.GONE)
            tuesdayTextViewHour1.setVisibility(View.GONE)
            tuesdayTextView1.setVisibility(View.GONE)
            tuesdayInputHour1.setVisibility(View.GONE)
            tuesdayInputMinute1.setVisibility(View.GONE)
            tuesdayInputHour1.text = null
            tuesdayInputMinute1.text = null
        } else if (tuesday == 2) {
            tuesday = 1
            tuesdayTextViewHour2.setVisibility(View.GONE)
            tuesdayTextView2.setVisibility(View.GONE)
            tuesdayInputHour2.setVisibility(View.GONE)
            tuesdayInputMinute2.setVisibility(View.GONE)
            tuesdayInputHour2.text = null
            tuesdayInputMinute2.text = null
        } else if (tuesday == 3) {
            tuesday = 2
            tuesdayTextViewHour3.setVisibility(View.GONE)
            tuesdayTextView3.setVisibility(View.GONE)
            tuesdayInputHour3.setVisibility(View.GONE)
            tuesdayInputMinute3.setVisibility(View.GONE)
            tuesdayInputHour3.text = null
            tuesdayInputMinute3.text = null
        }
    }

    private fun removeWednesday() {
        if (wednesday == 1) {
            wednesday = 0
            wednesdayButtonRemove.setVisibility(View.GONE)
            wednesdayText.setVisibility(View.GONE)
            wednesdayTextViewHour1.setVisibility(View.GONE)
            wednesdayTextView1.setVisibility(View.GONE)
            wednesdayInputHour1.setVisibility(View.GONE)
            wednesdayInputMinute1.setVisibility(View.GONE)
            wednesdayInputHour1.text = null
            wednesdayInputMinute1.text = null
        } else if (wednesday == 2) {
            wednesday = 1
            wednesdayTextViewHour2.setVisibility(View.GONE)
            wednesdayTextView2.setVisibility(View.GONE)
            wednesdayInputHour2.setVisibility(View.GONE)
            wednesdayInputMinute2.setVisibility(View.GONE)
            wednesdayInputHour2.text = null
            wednesdayInputMinute2.text = null
        } else if (wednesday == 3) {
            wednesday = 2
            wednesdayTextViewHour3.setVisibility(View.GONE)
            wednesdayTextView3.setVisibility(View.GONE)
            wednesdayInputHour3.setVisibility(View.GONE)
            wednesdayInputMinute3.setVisibility(View.GONE)
            wednesdayInputHour3.text = null
            wednesdayInputMinute3.text = null
        }
    }

    private fun removeThursday() {
        if (thursday == 1) {
            thursday = 0
            thursdayButtonRemove.setVisibility(View.GONE)
            thursdayText.setVisibility(View.GONE)
            thursdayTextViewHour1.setVisibility(View.GONE)
            thursdayTextView1.setVisibility(View.GONE)
            thursdayInputHour1.setVisibility(View.GONE)
            thursdayInputMinute1.setVisibility(View.GONE)
            thursdayInputHour1.text = null
            thursdayInputMinute1.text = null
        } else if (thursday == 2) {
            thursday = 1
            thursdayTextViewHour2.setVisibility(View.GONE)
            thursdayTextView2.setVisibility(View.GONE)
            thursdayInputHour2.setVisibility(View.GONE)
            thursdayInputMinute2.setVisibility(View.GONE)
            thursdayInputHour2.text = null
            thursdayInputMinute2.text = null
        } else if (thursday == 3) {
            thursday = 2
            thursdayTextViewHour3.setVisibility(View.GONE)
            thursdayTextView3.setVisibility(View.GONE)
            thursdayInputHour3.setVisibility(View.GONE)
            thursdayInputMinute3.setVisibility(View.GONE)
            thursdayInputHour3.text = null
            thursdayInputMinute3.text = null
        }
    }

    private fun removeFriday() {
        if (friday == 1) {
            friday = 0
            fridayButtonRemove.setVisibility(View.GONE)
            fridayText.setVisibility(View.GONE)
            fridayTextViewHour1.setVisibility(View.GONE)
            fridayTextView1.setVisibility(View.GONE)
            fridayInputHour1.setVisibility(View.GONE)
            fridayInputMinute1.setVisibility(View.GONE)
            fridayInputHour1.text = null
            fridayInputMinute1.text = null
        } else if (friday == 2) {
            friday = 1
            fridayTextViewHour2.setVisibility(View.GONE)
            fridayTextView2.setVisibility(View.GONE)
            fridayInputHour2.setVisibility(View.GONE)
            fridayInputMinute2.setVisibility(View.GONE)
            fridayInputHour2.text = null
            fridayInputMinute2.text = null
        } else if (friday == 3) {
            friday = 2
            fridayTextViewHour3.setVisibility(View.GONE)
            fridayTextView3.setVisibility(View.GONE)
            fridayInputHour3.setVisibility(View.GONE)
            fridayInputMinute3.setVisibility(View.GONE)
            fridayInputHour3.text = null
            fridayInputMinute3.text = null
        }
    }

    private fun removeSaturday() {
        if (saturday == 1) {
            saturday = 0
            saturdayButtonRemove.setVisibility(View.GONE)
            saturdayText.setVisibility(View.GONE)
            saturdayTextViewHour1.setVisibility(View.GONE)
            saturdayTextView1.setVisibility(View.GONE)
            saturdayInputHour1.setVisibility(View.GONE)
            saturdayInputMinute1.setVisibility(View.GONE)
            saturdayInputHour1.text = null
            saturdayInputMinute1.text = null
        } else if (saturday == 2) {
            saturday = 1
            saturdayTextViewHour2.setVisibility(View.GONE)
            saturdayTextView2.setVisibility(View.GONE)
            saturdayInputHour2.setVisibility(View.GONE)
            saturdayInputMinute2.setVisibility(View.GONE)
            saturdayInputHour2.text = null
            saturdayInputMinute2.text = null
        } else if (saturday == 3) {
            saturday = 2
            saturdayTextViewHour3.setVisibility(View.GONE)
            saturdayTextView3.setVisibility(View.GONE)
            saturdayInputHour3.setVisibility(View.GONE)
            saturdayInputMinute3.setVisibility(View.GONE)
            saturdayInputHour3.text = null
            saturdayInputMinute3.text = null
        }
    }

    private fun removeSunday() {
        if (sunday == 1) {
            sunday = 0
            sundayButtonRemove.setVisibility(View.GONE)
            sundayText.setVisibility(View.GONE)
            sundayTextViewHour1.setVisibility(View.GONE)
            sundayTextView1.setVisibility(View.GONE)
            sundayInputHour1.setVisibility(View.GONE)
            sundayInputMinute1.setVisibility(View.GONE)
            sundayInputHour1.text = null
            sundayInputMinute1.text = null
        } else if (sunday == 2) {
            sunday = 1
            sundayTextViewHour2.setVisibility(View.GONE)
            sundayTextView2.setVisibility(View.GONE)
            sundayInputHour2.setVisibility(View.GONE)
            sundayInputMinute2.setVisibility(View.GONE)
            sundayInputHour2.text = null
            sundayInputMinute2.text = null
        } else if (sunday == 3) {
            sunday = 2
            sundayTextViewHour3.setVisibility(View.GONE)
            sundayTextView3.setVisibility(View.GONE)
            sundayInputHour3.setVisibility(View.GONE)
            sundayInputMinute3.setVisibility(View.GONE)
            sundayInputHour3.text = null
            sundayInputMinute3.text = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun validatePillDetails(): Boolean {
        hours = arrayOf(inputHour1?.text.toString().toIntOrNull(), inputHour2?.text.toString().toIntOrNull(), inputHour3?.text.toString().toIntOrNull())
        hoursMonday = arrayOf(mondayInputHour1?.text.toString().toIntOrNull(), mondayInputHour2?.text.toString().toIntOrNull(), mondayInputHour3?.text.toString().toIntOrNull())
        hoursTuesday = arrayOf(tuesdayInputHour1?.text.toString().toIntOrNull(), tuesdayInputHour2?.text.toString().toIntOrNull(), tuesdayInputHour3?.text.toString().toIntOrNull())
        hoursWednesday = arrayOf(wednesdayInputHour1?.text.toString().toIntOrNull(), wednesdayInputHour2?.text.toString().toIntOrNull(), wednesdayInputHour3?.text.toString().toIntOrNull())
        hoursThursday = arrayOf(thursdayInputHour1?.text.toString().toIntOrNull(), thursdayInputHour2?.text.toString().toIntOrNull(), thursdayInputHour3?.text.toString().toIntOrNull())
        hoursFriday = arrayOf(fridayInputHour1?.text.toString().toIntOrNull(), fridayInputHour2?.text.toString().toIntOrNull(), fridayInputHour3?.text.toString().toIntOrNull())
        hoursSaturday = arrayOf(saturdayInputHour1?.text.toString().toIntOrNull(), saturdayInputHour2?.text.toString().toIntOrNull(), saturdayInputHour3?.text.toString().toIntOrNull())
        hoursSunday = arrayOf(sundayInputHour1?.text.toString().toIntOrNull(), sundayInputHour2?.text.toString().toIntOrNull(), sundayInputHour3?.text.toString().toIntOrNull())
        minutes = arrayOf(inputMinute1?.text.toString().toIntOrNull(), inputMinute2?.text.toString().toIntOrNull(), inputMinute3?.text.toString().toIntOrNull())
        minutesMonday = arrayOf(mondayInputMinute1?.text.toString().toIntOrNull(), mondayInputMinute2?.text.toString().toIntOrNull(), mondayInputMinute3?.text.toString().toIntOrNull())
        minutesTuesday = arrayOf(tuesdayInputMinute1?.text.toString().toIntOrNull(), tuesdayInputMinute2?.text.toString().toIntOrNull(), tuesdayInputMinute3?.text.toString().toIntOrNull())
        minutesWednesday = arrayOf(wednesdayInputMinute1?.text.toString().toIntOrNull(), wednesdayInputMinute2?.text.toString().toIntOrNull(), wednesdayInputMinute3?.text.toString().toIntOrNull())
        minutesThursday = arrayOf(thursdayInputMinute1?.text.toString().toIntOrNull(), thursdayInputMinute2?.text.toString().toIntOrNull(), thursdayInputMinute3?.text.toString().toIntOrNull())
        minutesFriday = arrayOf(fridayInputMinute1?.text.toString().toIntOrNull(), fridayInputMinute2?.text.toString().toIntOrNull(), fridayInputMinute3?.text.toString().toIntOrNull())
        minutesSaturday = arrayOf(saturdayInputMinute1?.text.toString().toIntOrNull(), saturdayInputMinute2?.text.toString().toIntOrNull(), saturdayInputMinute3?.text.toString().toIntOrNull())
        minutesSunday = arrayOf(sundayInputMinute1?.text.toString().toIntOrNull(), sundayInputMinute2?.text.toString().toIntOrNull(), sundayInputMinute3?.text.toString().toIntOrNull())
        amountLeft = inputLeft?.text.toString().toIntOrNull()
        amountInBox = inputPackage?.text.toString().toIntOrNull()

        if (TextUtils.isEmpty(pillName?.text.toString().trim())) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_pill_name), true)
            return false
        }

        for (hour in hours) {
            if (hour !== null && hour !in 1..24) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_hour), true)
                return false
            }
        }
        for (hour in hoursMonday) {
            if (hour !== null && hour !in 1..24) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_hour), true)
                return false
            }
        }
        for (hour in hoursTuesday) {
            if (hour !== null && hour !in 1..24) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_hour), true)
                return false
            }
        }
        for (hour in hoursWednesday) {
            if (hour !== null && hour !in 1..24) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_hour), true)
                return false
            }
        }
        for (hour in hoursFriday) {
            if (hour !== null && hour !in 1..24) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_hour), true)
                return false
            }
        }
        for (hour in hoursSaturday) {
            if (hour !== null && hour !in 1..24) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_hour), true)
                return false
            }
        }
        for (hour in hoursSunday) {
            if (hour !== null && hour !in 1..24) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_hour), true)
                return false
            }
        }
        for (hour in hoursThursday) {
            if (hour !== null && hour !in 1..24) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_hour), true)
                return false
            }
        }

        for (minute in minutes) {
            if (minute !== null && minute !in 0..59) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_minute), true)
                return false
            }
        }
        for (minute in minutesMonday) {
            if (minute !== null && minute !in 0..59) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_minute), true)
                return false
            }
        }
        for (minute in minutesTuesday) {
            if (minute !== null && minute !in 0..59) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_minute), true)
                return false
            }
        }
        for (minute in minutesWednesday) {
            if (minute !== null && minute !in 0..59) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_minute), true)
                return false
            }
        }
        for (minute in minutesThursday) {
            if (minute !== null && minute !in 0..59) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_minute), true)
                return false
            }
        }
        for (minute in minutesFriday) {
            if (minute !== null && minute !in 0..59) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_minute), true)
                return false
            }
        }
        for (minute in minutesSaturday) {
            if (minute !== null && minute !in 0..59) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_minute), true)
                return false
            }
        }
        for (minute in minutesSunday) {
            if (minute !== null && minute !in 0..59) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_minute), true)
                return false
            }
        }

        for (i in 0 until hours.size - 1) {
            val hour1 = hours[i]
            val minute1 = minutes[i]
            val hour2 = hours[i + 1]
            val minute2 = minutes[i + 1]

            if (hour1 != null && minute1 != null && hour2 != null && minute2 != null) {
                val time1 = LocalTime.of(hour1, minute1)
                val time2 = LocalTime.of(hour2, minute2)

                if (time1.isAfter(time2) || time1 == time2) {
                    showErrorSnackBar("Godziny muszą być podane chronologicznie", true)
                    return false
                }
            }
        }

        for (i in 0 until hoursMonday.size - 1) {
            val hour1 = hoursMonday[i]
            val minute1 = minutesMonday[i]
            val hour2 = hoursMonday[i + 1]
            val minute2 = minutesMonday[i + 1]

            if (hour1 != null && minute1 != null && hour2 != null && minute2 != null) {
                val time1 = LocalTime.of(hour1, minute1)
                val time2 = LocalTime.of(hour2, minute2)

                if (time1.isAfter(time2) || time1 == time2) {
                    showErrorSnackBar("Godziny muszą być podane chronologicznie", true)
                    return false
                }
            }
        }
        for (i in 0 until hoursTuesday.size - 1) {
            val hour1 = hoursTuesday[i]
            val minute1 = minutesTuesday[i]
            val hour2 = hoursTuesday[i + 1]
            val minute2 = minutesTuesday[i + 1]

            if (hour1 != null && minute1 != null && hour2 != null && minute2 != null) {
                val time1 = LocalTime.of(hour1, minute1)
                val time2 = LocalTime.of(hour2, minute2)

                if (time1.isAfter(time2) || time1 == time2) {
                    showErrorSnackBar("Godziny muszą być podane chronologicznie", true)
                    return false
                }
            }
        }
        for (i in 0 until hoursWednesday.size - 1) {
            val hour1 = hoursWednesday[i]
            val minute1 = minutesWednesday[i]
            val hour2 = hoursWednesday[i + 1]
            val minute2 = minutesWednesday[i + 1]

            if (hour1 != null && minute1 != null && hour2 != null && minute2 != null) {
                val time1 = LocalTime.of(hour1, minute1)
                val time2 = LocalTime.of(hour2, minute2)

                if (time1.isAfter(time2) || time1 == time2) {
                    showErrorSnackBar("Godziny muszą być podane chronologicznie", true)
                    return false
                }
            }
        }
        for (i in 0 until hoursThursday.size - 1) {
            val hour1 = hoursThursday[i]
            val minute1 = minutesThursday[i]
            val hour2 = hoursThursday[i + 1]
            val minute2 = minutesThursday[i + 1]

            if (hour1 != null && minute1 != null && hour2 != null && minute2 != null) {
                val time1 = LocalTime.of(hour1, minute1)
                val time2 = LocalTime.of(hour2, minute2)

                if (time1.isAfter(time2) || time1 == time2) {
                    showErrorSnackBar("Godziny muszą być podane chronologicznie", true)
                    return false
                }
            }
        }
        for (i in 0 until hoursFriday.size - 1) {
            val hour1 = hoursFriday[i]
            val minute1 = minutesFriday[i]
            val hour2 = hoursFriday[i + 1]
            val minute2 = minutesFriday[i + 1]

            if (hour1 != null && minute1 != null && hour2 != null && minute2 != null) {
                val time1 = LocalTime.of(hour1, minute1)
                val time2 = LocalTime.of(hour2, minute2)

                if (time1.isAfter(time2) || time1 == time2) {
                    showErrorSnackBar("Godziny muszą być podane chronologicznie", true)
                    return false
                }
            }
        }
        for (i in 0 until hoursSaturday.size - 1) {
            val hour1 = hoursSaturday[i]
            val minute1 = minutesSaturday[i]
            val hour2 = hoursSaturday[i + 1]
            val minute2 = minutesSaturday[i + 1]

            if (hour1 != null && minute1 != null && hour2 != null && minute2 != null) {
                val time1 = LocalTime.of(hour1, minute1)
                val time2 = LocalTime.of(hour2, minute2)

                if (time1.isAfter(time2) || time1 == time2) {
                    showErrorSnackBar("Godziny muszą być podane chronologicznie", true)
                    return false
                }
            }
        }
        for (i in 0 until hoursSunday.size - 1) {
            val hour1 = hoursSunday[i]
            val minute1 = minutesSunday[i]
            val hour2 = hoursSunday[i + 1]
            val minute2 = minutesSunday[i + 1]

            if (hour1 != null && minute1 != null && hour2 != null && minute2 != null) {
                val time1 = LocalTime.of(hour1, minute1)
                val time2 = LocalTime.of(hour2, minute2)

                if (time1.isAfter(time2) || time1 == time2) {
                    showErrorSnackBar("Godziny muszą być podane chronologicznie", true)
                    return false
                }
            }
        }

        if (amountLeft == null || amountLeft!! < 1 || amountLeft!! > amountInBox!!) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_amount), true)
            return false
        }

        if (amountInBox == null || amountInBox!! < 1) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_in_box), true)
            return false
        }

        return true
    }

    private fun defineNiestandardowa() {
        niestandardowaButtons = findViewById(R.id.niestandardowa)
        niestandardowaSeparator1 = findViewById(R.id.view3)
        niestandardowaSeparator2 = findViewById(R.id.view4)
        mondayButton = findViewById(R.id.monday)
        mondayButton.setOnClickListener{ addMonday() }
        thursdayButton = findViewById(R.id.thursday)
        thursdayButton.setOnClickListener{ addThursday() }
        tuesdayButton = findViewById(R.id.tuesday)
        tuesdayButton.setOnClickListener{ addTuesday() }
        wednesdayButton = findViewById(R.id.wednesday)
        wednesdayButton.setOnClickListener{ addWednesday() }
        fridayButton = findViewById(R.id.friday)
        fridayButton.setOnClickListener{ addFriday() }
        saturdayButton = findViewById(R.id.saturday)
        saturdayButton.setOnClickListener{ addSaturday() }
        sundayButton = findViewById(R.id.sunday)
        sundayButton.setOnClickListener{ addSunday() }
        mondayButtonRemove = findViewById(R.id.removeMonday)
        mondayButtonRemove.setOnClickListener{ removeMonday() }
        thursdayButtonRemove = findViewById(R.id.removeThursday)
        thursdayButtonRemove.setOnClickListener{ removeThursday() }
        tuesdayButtonRemove = findViewById(R.id.removeTuesday)
        tuesdayButtonRemove.setOnClickListener{ removeTuesday() }
        wednesdayButtonRemove = findViewById(R.id.removeWednesday)
        wednesdayButtonRemove.setOnClickListener{ removeWednesday() }
        fridayButtonRemove = findViewById(R.id.removeFriday)
        fridayButtonRemove.setOnClickListener{ removeFriday() }
        saturdayButtonRemove = findViewById(R.id.removeSaturday)
        saturdayButtonRemove.setOnClickListener{ removeSaturday() }
        sundayButtonRemove = findViewById(R.id.removeSunday)
        sundayButtonRemove.setOnClickListener{ removeSunday() }
        mondayButtonRemove.setVisibility(View.GONE)
        thursdayButtonRemove.setVisibility(View.GONE)
        tuesdayButtonRemove.setVisibility(View.GONE)
        wednesdayButtonRemove.setVisibility(View.GONE)
        fridayButtonRemove.setVisibility(View.GONE)
        saturdayButtonRemove.setVisibility(View.GONE)
        sundayButtonRemove.setVisibility(View.GONE)

        //MONDAY
        mondayText = findViewById(R.id.mondayText)
        mondayTextViewHour1 = findViewById(R.id.mondayTextViewHour1)
        mondayTextViewHour2 = findViewById(R.id.mondayTextViewHour2)
        mondayTextViewHour3 = findViewById(R.id.mondayTextViewHour3)
        mondayTextView1 = findViewById(R.id.mondayTextView1)
        mondayTextView2 = findViewById(R.id.mondayTextView2)
        mondayTextView3 = findViewById(R.id.mondayTextView3)
        mondayInputHour1 = findViewById(R.id.mondayHourTime1)
        mondayInputMinute1 = findViewById(R.id.mondayMinuteTime1)
        mondayInputHour2 = findViewById(R.id.mondayHourTime2)
        mondayInputMinute2 = findViewById(R.id.mondayMinuteTime2)
        mondayInputHour3 = findViewById(R.id.mondayHourTime3)
        mondayInputMinute3 = findViewById(R.id.mondayMinuteTime3)

        //TUESDAY
        tuesdayText = findViewById(R.id.tuesdayText)
        tuesdayTextViewHour1 = findViewById(R.id.tuesdayTextViewHour1)
        tuesdayTextViewHour2 = findViewById(R.id.tuesdayTextViewHour2)
        tuesdayTextViewHour3 = findViewById(R.id.tuesdayTextViewHour3)
        tuesdayTextView1 = findViewById(R.id.tuesdayTextView1)
        tuesdayTextView2 = findViewById(R.id.tuesdayTextView2)
        tuesdayTextView3 = findViewById(R.id.tuesdayTextView3)
        tuesdayInputHour1 = findViewById(R.id.tuesdayHourTime1)
        tuesdayInputMinute1 = findViewById(R.id.tuesdayMinuteTime1)
        tuesdayInputHour2 = findViewById(R.id.tuesdayHourTime2)
        tuesdayInputMinute2 = findViewById(R.id.tuesdayMinuteTime2)
        tuesdayInputHour3 = findViewById(R.id.tuesdayHourTime3)
        tuesdayInputMinute3 = findViewById(R.id.tuesdayMinuteTime3)

        //WEDNESDAY
        wednesdayText = findViewById(R.id.wednesdayText)
        wednesdayTextViewHour1 = findViewById(R.id.wednesdayTextViewHour1)
        wednesdayTextViewHour2 = findViewById(R.id.wednesdayTextViewHour2)
        wednesdayTextViewHour3 = findViewById(R.id.wednesdayTextViewHour3)
        wednesdayTextView1 = findViewById(R.id.wednesdayTextView1)
        wednesdayTextView2 = findViewById(R.id.wednesdayTextView2)
        wednesdayTextView3 = findViewById(R.id.wednesdayTextView3)
        wednesdayInputHour1 = findViewById(R.id.wednesdayHourTime1)
        wednesdayInputMinute1 = findViewById(R.id.wednesdayMinuteTime1)
        wednesdayInputHour2 = findViewById(R.id.wednesdayHourTime2)
        wednesdayInputMinute2 = findViewById(R.id.wednesdayMinuteTime2)
        wednesdayInputHour3 = findViewById(R.id.wednesdayHourTime3)
        wednesdayInputMinute3 = findViewById(R.id.wednesdayMinuteTime3)

        //THURSDAY
        thursdayText = findViewById(R.id.thursdayText)
        thursdayTextViewHour1 = findViewById(R.id.thursdayTextViewHour1)
        thursdayTextViewHour2 = findViewById(R.id.thursdayTextViewHour2)
        thursdayTextViewHour3 = findViewById(R.id.thursdayTextViewHour3)
        thursdayTextView1 = findViewById(R.id.thursdayTextView1)
        thursdayTextView2 = findViewById(R.id.thursdayTextView2)
        thursdayTextView3 = findViewById(R.id.thursdayTextView3)
        thursdayInputHour1 = findViewById(R.id.thursdayHourTime1)
        thursdayInputMinute1 = findViewById(R.id.thursdayMinuteTime1)
        thursdayInputHour2 = findViewById(R.id.thursdayHourTime2)
        thursdayInputMinute2 = findViewById(R.id.thursdayMinuteTime2)
        thursdayInputHour3 = findViewById(R.id.thursdayHourTime3)
        thursdayInputMinute3 = findViewById(R.id.thursdayMinuteTime3)

        //FRIDAY
        fridayText = findViewById(R.id.fridayText)
        fridayTextViewHour1 = findViewById(R.id.fridayTextViewHour1)
        fridayTextViewHour2 = findViewById(R.id.fridayTextViewHour2)
        fridayTextViewHour3 = findViewById(R.id.fridayTextViewHour3)
        fridayTextView1 = findViewById(R.id.fridayTextView1)
        fridayTextView2 = findViewById(R.id.fridayTextView2)
        fridayTextView3 = findViewById(R.id.fridayTextView3)
        fridayInputHour1 = findViewById(R.id.fridayHourTime1)
        fridayInputMinute1 = findViewById(R.id.fridayMinuteTime1)
        fridayInputHour2 = findViewById(R.id.fridayHourTime2)
        fridayInputMinute2 = findViewById(R.id.fridayMinuteTime2)
        fridayInputHour3 = findViewById(R.id.fridayHourTime3)
        fridayInputMinute3 = findViewById(R.id.fridayMinuteTime3)

        //SATURDAY
        saturdayText = findViewById(R.id.saturdayText)
        saturdayTextViewHour1 = findViewById(R.id.saturdayTextViewHour1)
        saturdayTextViewHour2 = findViewById(R.id.saturdayTextViewHour2)
        saturdayTextViewHour3 = findViewById(R.id.saturdayTextViewHour3)
        saturdayTextView1 = findViewById(R.id.saturdayTextView1)
        saturdayTextView2 = findViewById(R.id.saturdayTextView2)
        saturdayTextView3 = findViewById(R.id.saturdayTextView3)
        saturdayInputHour1 = findViewById(R.id.saturdayHourTime1)
        saturdayInputMinute1 = findViewById(R.id.saturdayMinuteTime1)
        saturdayInputHour2 = findViewById(R.id.saturdayHourTime2)
        saturdayInputMinute2 = findViewById(R.id.saturdayMinuteTime2)
        saturdayInputHour3 = findViewById(R.id.saturdayHourTime3)
        saturdayInputMinute3 = findViewById(R.id.saturdayMinuteTime3)

        //SUNDAY
        sundayText = findViewById(R.id.sundayText)
        sundayTextViewHour1 = findViewById(R.id.sundayTextViewHour1)
        sundayTextViewHour2 = findViewById(R.id.sundayTextViewHour2)
        sundayTextViewHour3 = findViewById(R.id.sundayTextViewHour3)
        sundayTextView1 = findViewById(R.id.sundayTextView1)
        sundayTextView2 = findViewById(R.id.sundayTextView2)
        sundayTextView3 = findViewById(R.id.sundayTextView3)
        sundayInputHour1 = findViewById(R.id.sundayHourTime1)
        sundayInputMinute1 = findViewById(R.id.sundayMinuteTime1)
        sundayInputHour2 = findViewById(R.id.sundayHourTime2)
        sundayInputMinute2 = findViewById(R.id.sundayMinuteTime2)
        sundayInputHour3 = findViewById(R.id.sundayHourTime3)
        sundayInputMinute3 = findViewById(R.id.sundayMinuteTime3)
    }

    private fun closeNiestandardowaView() {
        niestandardowaButtons.setVisibility(View.GONE);
        niestandardowaSeparator1.setVisibility(View.GONE);
        niestandardowaSeparator2.setVisibility(View.GONE);

        inputHour1.setVisibility(View.VISIBLE);
        inputMinute1.setVisibility(View.VISIBLE);
        text1.setVisibility(View.VISIBLE);
        text11.setVisibility(View.VISIBLE);

        mondayText.setVisibility(View.GONE)
        mondayTextViewHour1.setVisibility(View.GONE)
        mondayTextView1.setVisibility(View.GONE)
        mondayInputHour1.setVisibility(View.GONE)
        mondayInputMinute1.setVisibility(View.GONE)
        mondayTextViewHour2.setVisibility(View.GONE)
        mondayTextView2.setVisibility(View.GONE)
        mondayInputHour2.setVisibility(View.GONE)
        mondayInputMinute2.setVisibility(View.GONE)
        mondayTextViewHour3.setVisibility(View.GONE)
        mondayTextView3.setVisibility(View.GONE)
        mondayInputHour3.setVisibility(View.GONE)
        mondayInputMinute3.setVisibility(View.GONE)

        tuesdayText.setVisibility(View.GONE)
        tuesdayTextViewHour1.setVisibility(View.GONE)
        tuesdayTextView1.setVisibility(View.GONE)
        tuesdayInputHour1.setVisibility(View.GONE)
        tuesdayInputMinute1.setVisibility(View.GONE)
        tuesdayTextViewHour2.setVisibility(View.GONE)
        tuesdayTextView2.setVisibility(View.GONE)
        tuesdayInputHour2.setVisibility(View.GONE)
        tuesdayInputMinute2.setVisibility(View.GONE)
        tuesdayTextViewHour3.setVisibility(View.GONE)
        tuesdayTextView3.setVisibility(View.GONE)
        tuesdayInputHour3.setVisibility(View.GONE)
        tuesdayInputMinute3.setVisibility(View.GONE)

        wednesdayText.setVisibility(View.GONE)
        wednesdayTextViewHour1.setVisibility(View.GONE)
        wednesdayTextView1.setVisibility(View.GONE)
        wednesdayInputHour1.setVisibility(View.GONE)
        wednesdayInputMinute1.setVisibility(View.GONE)
        wednesdayTextViewHour2.setVisibility(View.GONE)
        wednesdayTextView2.setVisibility(View.GONE)
        wednesdayInputHour2.setVisibility(View.GONE)
        wednesdayInputMinute2.setVisibility(View.GONE)
        wednesdayTextViewHour3.setVisibility(View.GONE)
        wednesdayTextView3.setVisibility(View.GONE)
        wednesdayInputHour3.setVisibility(View.GONE)
        wednesdayInputMinute3.setVisibility(View.GONE)

        thursdayText.setVisibility(View.GONE)
        thursdayTextViewHour1.setVisibility(View.GONE)
        thursdayTextView1.setVisibility(View.GONE)
        thursdayInputHour1.setVisibility(View.GONE)
        thursdayInputMinute1.setVisibility(View.GONE)
        thursdayTextViewHour2.setVisibility(View.GONE)
        thursdayTextView2.setVisibility(View.GONE)
        thursdayInputHour2.setVisibility(View.GONE)
        thursdayInputMinute2.setVisibility(View.GONE)
        thursdayTextViewHour3.setVisibility(View.GONE)
        thursdayTextView3.setVisibility(View.GONE)
        thursdayInputHour3.setVisibility(View.GONE)
        thursdayInputMinute3.setVisibility(View.GONE)

        fridayText.setVisibility(View.GONE)
        fridayTextViewHour1.setVisibility(View.GONE)
        fridayTextView1.setVisibility(View.GONE)
        fridayInputHour1.setVisibility(View.GONE)
        fridayInputMinute1.setVisibility(View.GONE)
        fridayTextViewHour2.setVisibility(View.GONE)
        fridayTextView2.setVisibility(View.GONE)
        fridayInputHour2.setVisibility(View.GONE)
        fridayInputMinute2.setVisibility(View.GONE)
        fridayTextViewHour3.setVisibility(View.GONE)
        fridayTextView3.setVisibility(View.GONE)
        fridayInputHour3.setVisibility(View.GONE)
        fridayInputMinute3.setVisibility(View.GONE)

        saturdayText.setVisibility(View.GONE)
        saturdayTextViewHour1.setVisibility(View.GONE)
        saturdayTextView1.setVisibility(View.GONE)
        saturdayInputHour1.setVisibility(View.GONE)
        saturdayInputMinute1.setVisibility(View.GONE)
        saturdayTextViewHour2.setVisibility(View.GONE)
        saturdayTextView2.setVisibility(View.GONE)
        saturdayInputHour2.setVisibility(View.GONE)
        saturdayInputMinute2.setVisibility(View.GONE)
        saturdayTextViewHour3.setVisibility(View.GONE)
        saturdayTextView3.setVisibility(View.GONE)
        saturdayInputHour3.setVisibility(View.GONE)
        saturdayInputMinute3.setVisibility(View.GONE)

        sundayText.setVisibility(View.GONE)
        sundayTextViewHour1.setVisibility(View.GONE)
        sundayTextView1.setVisibility(View.GONE)
        sundayInputHour1.setVisibility(View.GONE)
        sundayInputMinute1.setVisibility(View.GONE)
        sundayTextViewHour2.setVisibility(View.GONE)
        sundayTextView2.setVisibility(View.GONE)
        sundayInputHour2.setVisibility(View.GONE)
        sundayInputMinute2.setVisibility(View.GONE)
        sundayTextViewHour3.setVisibility(View.GONE)
        sundayTextView3.setVisibility(View.GONE)
        sundayInputHour3.setVisibility(View.GONE)
        sundayInputMinute3.setVisibility(View.GONE)
    }
}