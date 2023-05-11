package com.example.myapp.patients_list

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageButton
import com.example.myapp.R
import com.example.myapp.login.BaseActivity
import com.example.myapp.login.UserModel
import com.example.myapp.settings.DoctorSettingsActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.UUID

class DoctorAddPatientsActivity : BaseActivity(), View.OnClickListener {

    private var inputEmail: EditText? = null
    private var inputFirstName: EditText? = null
    private var inputLastName: EditText? = null
    private var addButton: Button? = null
    private var backButton: AppCompatImageButton? = null
    private lateinit var dbRef: DatabaseReference
    private var patientId = ""
    private lateinit var patientIds: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)

        patientIds = intent.getStringArrayExtra("patientIds")!!

        inputFirstName = findViewById(R.id.patientFirstName)
        inputLastName = findViewById(R.id.patientLastName)
        inputEmail = findViewById(R.id.patientEmail)
        addButton = findViewById(R.id.addPatient)
        addButton?.setOnClickListener(this)

        backButton = findViewById(R.id.close)
        backButton?.setOnClickListener(this)
    }

    private fun validateDetails(): Boolean {
        return when {
            TextUtils.isEmpty(inputFirstName?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }
            TextUtils.isEmpty(inputLastName?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }
            TextUtils.isEmpty(inputEmail?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
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
                        ifUserExist().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                if (task.result) {
                                    val intent = Intent(this, DoctorSettingsActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }

                R.id.close -> {
                    val intent = Intent(this, DoctorSettingsActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun ifUserExist(): Task<Boolean> {
        var exists = false
        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        val query = dbRef.orderByChild("email").equalTo(inputEmail!!.text.toString())
        return query.get().continueWith { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot.exists()) {
                    val user = snapshot.children.first().getValue(UserModel::class.java)

                    if (user!!.lastName.equals(inputLastName!!.text.toString()) && user!!.firstName.equals(inputFirstName!!.text.toString())) {
                        exists = true
                        patientId = user.id

                        if (patientId in patientIds) {
                            showErrorSnackBar("Dany użytkownik jest już zapisany", true)
                            exists = false
                        } else {
                            addToDatabase()
                        }
                    } else {
                        showErrorSnackBar("Dane użytkownika nie są zgodne", true)
                    }
                } else {
                    showErrorSnackBar("Dane użytkownika nie są zgodne", true)
                }
            } else {
                Log.d("TAG", "Błąd w pobieraniu danych")
            }
            exists
        }
    }


    private fun addToDatabase() {
        dbRef = FirebaseDatabase.getInstance().getReference("Patients")
        val new = PatientDoctorModel(FirebaseAuth.getInstance().uid.toString(), patientId)

        dbRef.child(UUID.randomUUID().toString()).setValue(new)
        showErrorSnackBar("Pomyślnie dodano pacjenta", false)
    }
}