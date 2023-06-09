package com.example.myapp.doctor_view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.R
import com.example.myapp.SharedObject
import com.example.myapp.login.UserModel
import com.example.myapp.patients_list.ViewPatientsActivity
import com.example.myapp.settings.DoctorSettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PatientActionsActivity : AppCompatActivity(), View.OnClickListener {

    private var patientId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_doctor_actions)

        val backButton = findViewById<ImageButton>(R.id.back)
        backButton.setOnClickListener(this)

        val pillsButton = findViewById<ImageButton>(R.id.choosePills)
        pillsButton.setOnClickListener(this)

        val reportButton = findViewById<ImageButton>(R.id.chooseReport)
        reportButton.setOnClickListener(this)

        patientId = intent.getStringExtra("patientId")
        val patientName = findViewById<TextView>(R.id.patientName)

        getDataFromDatabase(patientName)

        val navView: BottomNavigationView = findViewById(R.id.navigation_bar)

        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@PatientActionsActivity, ViewPatientsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_settings -> {
                    val intent = Intent(this@PatientActionsActivity, DoctorSettingsActivity::class.java)
                    intent.putExtra("patientIds", SharedObject.getlistPacientIds())
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

    }

    override fun onClick(view: View?) {
        if(view !=null){
            when (view.id){

                R.id.back ->{
                    val intent = Intent(this, ViewPatientsActivity::class.java)
                    startActivity(intent)
                }

                R.id.choosePills ->{
                    val intent = Intent(this, PatientPillsActivity::class.java)
                    intent.putExtra("patientId", patientId)
                    startActivity(intent)
                }
                R.id.chooseReport ->{
                    val intent = Intent(this, MonthyReportDoctor::class.java)
                    intent.putExtra("patientId", patientId)
                    startActivity(intent)
                }
            }
        }
    }

    private fun getDataFromDatabase(patientName: TextView) {
        val pacientRef = FirebaseDatabase.getInstance().getReference("Users").child(patientId.toString())
        pacientRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val patient = dataSnapshot.getValue(UserModel::class.java)
                if (patient != null) {
                    val patientFirstName = patient.firstName
                    val patientLastName = patient.lastName
                    patientName.setText(patientFirstName + " " + patientLastName)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
    }
}