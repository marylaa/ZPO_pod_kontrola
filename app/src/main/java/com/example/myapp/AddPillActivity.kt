package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.example.myapp.login.BaseActivity
import com.example.myapp.login.LoginActivity
import com.example.myapp.pills_list.UserScheduleActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class AddPillActivity : BaseActivity()  {

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
        setContentView(R.layout.activity_add_pill)

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
        val intent = Intent(this, UserScheduleActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun savePill() {
        dbRef = FirebaseDatabase.getInstance().getReference("Pills")

        val name = inputName?.text.toString().trim() { it <= ' ' }
        val hour = inputHour?.text.toString().toIntOrNull()
        val minute = inputMinute?.text.toString().toIntOrNull()
        val amountLeft = inputLeft?.text.toString().toIntOrNull()
        val amountBox = inputPackage?.text.toString().toIntOrNull()
        val frequency = selectedFrequency

        val user = FirebaseAuth.getInstance().currentUser;
        val uid = user?.uid

        val newPill = PillModel(uid, name, amountLeft, amountBox, frequency, hour, minute)

        dbRef.child(UUID.randomUUID().toString()).setValue(newPill)
    }
}