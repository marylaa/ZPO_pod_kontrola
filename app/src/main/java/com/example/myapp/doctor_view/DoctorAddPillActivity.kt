package com.example.myapp.doctor_view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.example.myapp.R
import com.example.myapp.login.BaseActivity
import com.example.myapp.patients_list.ViewPatientsActivity
import com.example.myapp.pills_list.PillModel
import com.example.myapp.settings.DoctorSettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pill_doctor)

        patientId = intent.getStringExtra("patientId")

        val backButton = findViewById<ImageButton>(R.id.close)
        backButton.setOnClickListener(this)

        saveButton = findViewById(R.id.savePill)
        inputName = findViewById(R.id.pillName)
        val inputHour1 = findViewById<EditText>(R.id.hourTime1)
        val inputMinute1 = findViewById<EditText>(R.id.minuteTime1)
        val inputHour2 = findViewById<EditText>(R.id.hourTime2)
        val inputMinute2 = findViewById<EditText>(R.id.minuteTime2)
        val inputHour3 = findViewById<EditText>(R.id.hourTime3)
        val inputMinute3 = findViewById<EditText>(R.id.minuteTime3)
        val text2 = findViewById<TextView>(R.id.textViewHour2)
        val text22 = findViewById<TextView>(R.id.textView2)
        val text3 = findViewById<TextView>(R.id.textViewHour3)
        val text33 = findViewById<TextView>(R.id.textView3)
        inputLeft = findViewById(R.id.amountLeft)
        inputPackage = findViewById(R.id.inBox)

        saveButton?.setOnClickListener{
            if (validatePillDetails(inputHour1, inputMinute1)) {
                savePill(inputHour1, inputMinute1)
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
                } else {
                    inputHour2.setVisibility(View.GONE);
                    inputMinute2.setVisibility(View.GONE);
                    text2.setVisibility(View.GONE);
                    text22.setVisibility(View.GONE);

                    inputHour3.setVisibility(View.GONE);
                    inputMinute3.setVisibility(View.GONE);
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

    private fun validatePillDetails(inputHour1: EditText, inputMinute1: EditText): Boolean {

        val hour = inputHour?.text.toString().toIntOrNull()
        val minute = inputMinute?.text.toString().toIntOrNull()
        val amountLeft = inputLeft?.text.toString().toIntOrNull()
        val amountInBox = inputPackage?.text.toString().toIntOrNull()

        return when {
            TextUtils.isEmpty(inputName?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_pill_name), true)
                false
            }
            hour == null || hour !in 1..24 -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_hour), true)
                false
            }
            minute == null || minute !in 0..59 -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_minute), true)
                false
            }
            amountLeft == null || amountLeft < 1 || amountLeft > amountInBox!!.toInt() -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_amount), true)
                false
            }
            amountInBox == null || amountInBox < 1  -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_valid_in_box), true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun goToSchedule(view: View) {
        val intent = Intent(this, PatientPillsActivity::class.java)
        intent.putExtra("patientId", patientId)
        startActivity(intent)
        finish()
    }

    private fun savePill(inputHour1: EditText, inputMinute1: EditText) {
        dbRef = FirebaseDatabase.getInstance().getReference("Pills")

        val name = inputName?.text.toString().trim() { it <= ' ' }
        val hour = inputHour?.text.toString().toIntOrNull()
        val minute = inputMinute?.text.toString().toIntOrNull()
        val amountLeft = inputLeft?.text.toString().toIntOrNull()
        val amountBox = inputPackage?.text.toString().toIntOrNull()
        val frequency = selectedFrequency

        val id = UUID.randomUUID().toString()
        val newPill = PillModel(id, patientId, name, amountLeft, amountBox, frequency, hour, minute, false)

        Log.d("LEK", newPill.toString())
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
}