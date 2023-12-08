package com.example.myapp.patients_list

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import com.example.myapp.R
import com.example.myapp.login.BaseActivity
import com.example.myapp.login.UserModel
import com.example.myapp.settings.DoctorSettingsActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.UUID

class DoctorAddPatientsActivity : BaseActivity(), View.OnClickListener {

    private var inputPesel: EditText? = null
    private var addButton: Button? = null
    private var searchButton: Button? = null
    private var backButton: AppCompatImageButton? = null
    private lateinit var dbRef: DatabaseReference
    private var patientId = ""
    private lateinit var patientIds: Array<String>

    private var patientLastName: TextView? = null
    private var patientFirstName: TextView? = null
    private var patientEmail: TextView? = null
    private var patientPesel: TextView? = null
    private var LastName: TextView? = null
    private var FirstName: TextView? = null
    private var Email: TextView? = null
    private var Pesel: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)

        patientIds = intent.getStringArrayExtra("patientIds")!!

        inputPesel = findViewById(R.id.patientPesel)
        addButton = findViewById(R.id.addPatient)
        patientLastName = findViewById(R.id.nazwiskoPatient)
        patientFirstName = findViewById(R.id.imięPatient)
        patientEmail = findViewById(R.id.emailPatient)
        patientPesel = findViewById(R.id.peselPatient)
        LastName = findViewById(R.id.nazwisko)
        FirstName = findViewById(R.id.imię)
        Email = findViewById(R.id.email)
        Pesel = findViewById(R.id.pesel)
        addButton?.setOnClickListener(this)
        searchButton = findViewById(R.id.searchPatient)
        searchButton?.setOnClickListener(this)

        backButton = findViewById(R.id.close)
        backButton?.setOnClickListener(this)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@DoctorAddPatientsActivity, ViewPatientsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_settings -> {
                    val intent = Intent(this@DoctorAddPatientsActivity, DoctorSettingsActivity::class.java)
                    intent.putExtra("patientIds", patientIds)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun validateDetails(): Boolean {
        return when {
            TextUtils.isEmpty(inputPesel?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_pesel), true)
                false
            }
            (inputPesel?.text.toString().length !== 11) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_wrong_pesel),true)
                false
            }
            else -> {
                true
            }
        }
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {

                R.id.addPatient -> {
                    if (validateDetails()) {
                        addPatient().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                if (task.result) {
                                    Toast.makeText(this@DoctorAddPatientsActivity, "Pacjent został dodany", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, DoctorSettingsActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }

                R.id.searchPatient -> {
                    if (validateDetails()) {
                        ifUserExist().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                if (task.result) {
                                    Toast.makeText(this@DoctorAddPatientsActivity, "Pacjent został dodany", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, DoctorSettingsActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }

                R.id.close -> {
                    val intent = Intent(this, DoctorSettingsActivity::class.java)
                    intent.putExtra("patientIds", patientIds)
                    startActivity(intent)
                }
            }
        }
    }

    private fun ifUserExist(): Task<Boolean> {
        var exists = false
        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        val query = dbRef.orderByChild("pesel").equalTo(inputPesel!!.text.toString())
        return query.get().continueWith { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot.exists()) {
                    val user = snapshot.children.first().getValue(UserModel::class.java)
                    patientId = user!!.id

                    patientFirstName?.setText(user.firstName)
                    patientLastName?.setText(user.lastName)
                    patientPesel?.setText(user.pesel)
                    patientEmail?.setText(user.email)
                    patientFirstName?.setVisibility(View.VISIBLE);
                    patientLastName?.setVisibility(View.VISIBLE);
                    patientPesel?.setVisibility(View.VISIBLE);
                    patientEmail?.setVisibility(View.VISIBLE);
                    LastName?.setVisibility(View.VISIBLE);
                    FirstName?.setVisibility(View.VISIBLE);
                    Email?.setVisibility(View.VISIBLE);
                    Pesel?.setVisibility(View.VISIBLE);
                    addButton?.setVisibility(View.VISIBLE);

                } else {
                    showErrorSnackBar("Dane użytkownika nie są zgodne", true)
                }
            } else {
                Log.d("TAG", task.exception.toString())
            }
            exists
        }
    }

    private fun addPatient(): Task<Boolean> {
        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        var added = false

        val query = dbRef.orderByChild("pesel").equalTo(inputPesel!!.text.toString())
        return query.get().continueWith { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot.exists()) {
                    val user = snapshot.children.first().getValue(UserModel::class.java)
                    patientId = user!!.id
                    
                    if (patientId in patientIds) {
                        showErrorSnackBar("Dany użytkownik jest już zapisany", true)
                    } else {
                        addToDatabase()
                        added = true
                    }
                } else {

                }
            } else {
                Log.d("TAG", "Błąd w pobieraniu danych")
            }
            added
        }
    }


    private fun addToDatabase() {
        dbRef = FirebaseDatabase.getInstance().getReference("Patients")
        val id = UUID.randomUUID().toString()
        val new = PatientDoctorModel(FirebaseAuth.getInstance().uid.toString(), patientId, id)

        dbRef.child(id).setValue(new)
    }
}