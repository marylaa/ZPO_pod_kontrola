package com.example.myapp.doctor_view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.myapp.R
import com.example.myapp.login.BaseActivity
import com.example.myapp.patients_list.ViewPatientsActivity
import com.example.myapp.pills_list.AddPillActivity
import com.example.myapp.pills_list.PillModel
import com.example.myapp.settings.DoctorSettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DoctorAddPillActivity : BaseActivity(), View.OnClickListener {

    private var patientId: String? = null
    private var saveButton: Button? = null
    private var inputName: EditText? = null
    private var inputHour: EditText? = null
    private var inputMinute: EditText? = null
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pill_doctor)

        patientId = intent.getStringExtra("patientId")

        val backButton = findViewById<ImageButton>(R.id.close)
        backButton.setOnClickListener(this)

        saveButton = findViewById(R.id.savePill)
        inputName = findViewById(R.id.pillName)
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
        inputLeft = findViewById(R.id.amountLeft)
        inputPackage = findViewById(R.id.inBox)

        saveButton?.setOnClickListener{
            if (validatePillDetails()) {
                savePill()
                finish()
            }
        }

        val spinner = findViewById<Spinner>(R.id.spinner1)
        val elements = arrayOf("Codziennie", "Dwa razy dziennie", "Trzy razy dziennie", "Co drugi dzień", "Raz w tygodniu")
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
                if (selectedFrequency.equals("Dwa razy dziennie")) {
                    inputHour2.setVisibility(View.VISIBLE);
                    inputMinute2.setVisibility(View.VISIBLE);
                    text2.setVisibility(View.VISIBLE);
                    text22.setVisibility(View.VISIBLE);
                } else if (selectedFrequency.equals("Trzy razy dziennie")) {
                    inputHour3.setVisibility(View.VISIBLE);
                    inputMinute3.setVisibility(View.VISIBLE);
                    text3.setVisibility(View.VISIBLE);
                    text33.setVisibility(View.VISIBLE);
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@DoctorAddPillActivity, ViewPatientsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_settings -> {
                    val intent = Intent(this@DoctorAddPillActivity, DoctorSettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

    }

    private fun validatePillDetails(): Boolean {
        hours = arrayOf(inputHour1?.text.toString().toIntOrNull(), inputHour2?.text.toString().toIntOrNull(), inputHour3?.text.toString().toIntOrNull())
        minutes = arrayOf(inputMinute1?.text.toString().toIntOrNull(), inputMinute2?.text.toString().toIntOrNull(), inputMinute3?.text.toString().toIntOrNull())
        amountLeft = inputLeft?.text.toString().toIntOrNull()
        amountInBox = inputPackage?.text.toString().toIntOrNull()

        if (TextUtils.isEmpty(inputName?.text.toString().trim())) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_pill_name), true)
            return false
        }

        for (hour in hours) {
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

    fun goToSchedule(view: View) {
        val intent = Intent(this, PatientPillsActivity::class.java)
        intent.putExtra("patientId", patientId)
        startActivity(intent)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun savePill() {
        dbRef = FirebaseDatabase.getInstance().getReference("Pills")

        val current = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = current.format(formatter)

        val name = inputName?.text.toString().trim() { it <= ' ' }
        val frequency = selectedFrequency
        val time1 = timeToString(hours.get(0), minutes.get(0))
        val times1 = listOf(time1, false)
        var times: List<List<Any>>? = null

        if (selectedFrequency.equals("Dwa razy dziennie")) {
            val time2 = timeToString(hours.get(1), minutes.get(1))
            val times2 = listOf(time2, false)
            times = listOf(times1, times2)
        } else if (selectedFrequency.equals("Trzy razy dziennie")) {
            val time2 = timeToString(hours.get(1), minutes.get(1))
            val time3 = timeToString(hours.get(2), minutes.get(2))
            val times2 = listOf(time2, false)
            val times3 = listOf(time3, false)
            times = listOf(times1, times2, times3)
        } else {
            times = listOf(times1)
        }

        val id = UUID.randomUUID().toString()
        val newPill = PillModel(id, patientId, name, amountLeft, amountInBox, frequency, times, date)

        dbRef.child(id).setValue(newPill)
    }

    override fun onClick(view: View?) {
        if(view !=null){
            when (view.id){

                R.id.close ->{
                    val intent = Intent(this, PatientPillsActivity::class.java)
                    intent.putExtra("patientId", patientId)
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
            minute_new = minute.toString()
        }
        return hour_new + ":" + minute_new
    }
}