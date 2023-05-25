package com.example.myapp.pills_list

import android.content.Intent
import android.os.AsyncTask
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class EditPillActivity : BaseActivity(), View.OnClickListener {

    private var pillId: String? = null
    private var pill: PillModel? = null
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
                savePill()
                finish()
            }
        }

        pillId = intent.getStringExtra("pillId")
        dbRef = FirebaseDatabase.getInstance().getReference("Pills")
        dbRef.child(pillId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pill = snapshot.getValue(PillModel::class.java)

                pill?.let {
                    pillName?.setText(it.name)
                    inputLeft?.setText(it.availability.toString())
                    inputPackage?.setText(it.inBox.toString())
                    val hour1 = it.time_list!!.get(0).get(0).toString().split(":")
                    inputHour1?.setText(hour1.get(0))
                    inputMinute1?.setText(hour1.get(1))

                    val spinner = findViewById<Spinner>(R.id.spinner1)
                    when (it.frequency) {
                        "Codziennie" -> spinner.setSelection(0)
                        "Dwa razy dziennie" -> {
                            spinner.setSelection(1)
                            inputHour2.setVisibility(View.VISIBLE);
                            inputMinute2.setVisibility(View.VISIBLE);
                            text2.setVisibility(View.VISIBLE);
                            text22.setVisibility(View.VISIBLE);

                            val hour2 = it.time_list!!.get(1).get(0).toString().split(":")
                            inputHour2?.setText(hour2.get(0))
                            inputMinute2?.setText(hour2.get(1))
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

                            val hour2 = it.time_list!!.get(1).get(0).toString().split(":")
                            inputHour2?.setText(hour2.get(0))
                            inputMinute2?.setText(hour2.get(1))

                            val hour3 = it.time_list!!.get(2).get(0).toString().split(":")
                            inputHour3?.setText(hour3.get(0))
                            inputMinute3?.setText(hour3.get(1))
                        }
                        "Co drugi dzień" -> spinner.setSelection(3)
                        "Raz w tygodniu" -> spinner.setSelection(4)
                        else -> {}
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })

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

                    inputHour3.setVisibility(View.GONE);
                    inputHour3.text = null
                    inputMinute3.setVisibility(View.GONE);
                    inputMinute3.text = null
                    text3.setVisibility(View.GONE);
                    text33.setVisibility(View.GONE);
                } else if (selectedFrequency.equals("Trzy razy dziennie")) {
                    inputHour2.setVisibility(View.VISIBLE);
                    inputMinute2.setVisibility(View.VISIBLE);
                    text2.setVisibility(View.VISIBLE);
                    text22.setVisibility(View.VISIBLE);
                    inputHour3.setVisibility(View.VISIBLE);
                    inputMinute3.setVisibility(View.VISIBLE);
                    text3.setVisibility(View.VISIBLE);
                    text33.setVisibility(View.VISIBLE);
                }  else {
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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun validatePillDetails(): Boolean {
        hours = arrayOf(inputHour1?.text.toString().toIntOrNull(), inputHour2?.text.toString().toIntOrNull(), inputHour3?.text.toString().toIntOrNull())
        minutes = arrayOf(inputMinute1?.text.toString().toIntOrNull(), inputMinute2?.text.toString().toIntOrNull(), inputMinute3?.text.toString().toIntOrNull())
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

        for (minute in minutes) {
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
        val intent = Intent(this, UserScheduleActivity::class.java)
        startActivity(intent)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun savePill() {
        dbRef = FirebaseDatabase.getInstance().getReference("Pills")

        val current = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = current.format(formatter)

        pill!!.name = pillName?.text.toString().trim() { it <= ' ' }
        pill!!.frequency = selectedFrequency
        pill!!.date_last = date
        pill!!.availability = inputLeft?.text.toString().toIntOrNull()
        pill!!.inBox = inputPackage?.text.toString().toIntOrNull()

        val time1 = timeToString(hours.get(0), minutes.get(0))
        val times1 = mutableListOf<Any?>(time1, false)
        var times: MutableList<MutableList<Any?>>? = null

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
        } else {
            times = mutableListOf(times1)
        }
        pill!!.time_list = times

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

        dbRef.child(pill!!.id.toString()).setValue(pill)
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
}