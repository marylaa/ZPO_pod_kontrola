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
import com.google.firebase.database.*

class DoctorEditPillActivity : BaseActivity(), View.OnClickListener {

    private var patientId: String? = null
    private var pillId: String? = null
    private var pill: PillModel? = null
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
        setContentView(R.layout.activity_edit_pill_doctor)

        patientId = intent.getStringExtra("patientId")

        val backButton = findViewById<ImageButton>(R.id.close)
        backButton.setOnClickListener(this)

        saveButton = findViewById(R.id.savePill)
        inputName = findViewById(R.id.pillName)
        inputHour = findViewById(R.id.hourTime)
        inputMinute = findViewById(R.id.minuteTime)
        inputLeft = findViewById(R.id.amountLeft)
        inputPackage = findViewById(R.id.inBox)

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
                    inputName?.setText(it.name)
                    inputHour?.setText(it.hour.toString())
                    inputMinute?.setText(it.minute.toString())
                    inputLeft?.setText(it.availability.toString())
                    inputPackage?.setText(it.inBox.toString())

                    val spinner = findViewById<Spinner>(R.id.spinner1)
                    when (it.frequency) {
                        "Codziennie" -> spinner.setSelection(0)
                        "Co drugi dzień" -> spinner.setSelection(1)
                        "Raz w tygodniu" -> spinner.setSelection(2)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })

        val spinner = findViewById<Spinner>(R.id.spinner1)
        val elements = arrayOf("Codziennie", "Co drugi dzień", "Raz w tygodniu")
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
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)

        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@DoctorEditPillActivity, ViewPatientsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_settings -> {
                    val intent = Intent(this@DoctorEditPillActivity, DoctorSettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

    }

    private fun validatePillDetails(): Boolean {

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

    private fun savePill() {
        dbRef = FirebaseDatabase.getInstance().getReference("Pills")

        pill!!.name = inputName?.text.toString().trim() { it <= ' ' }
        pill!!.hour = inputHour?.text.toString().toIntOrNull()
        pill!!.minute = inputMinute?.text.toString().toIntOrNull()
        pill!!.availability = inputLeft?.text.toString().toIntOrNull()
        pill!!.inBox = inputPackage?.text.toString().toIntOrNull()
        pill!!.frequency = selectedFrequency

        dbRef.child(pill!!.id.toString()).setValue(pill)
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