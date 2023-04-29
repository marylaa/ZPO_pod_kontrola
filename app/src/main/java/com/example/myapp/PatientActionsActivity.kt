package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.login.UserModel
import com.example.myapp.patients_list.ViewPatientsActivity
import com.example.myapp.settings.DoctorSettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PatientActionsActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_doctor_actions)

        val backButton = findViewById<ImageButton>(R.id.back)
        backButton.setOnClickListener(this)

        val patientId = intent.getStringExtra("patientId")
        val patientName = findViewById<TextView>(R.id.patientName)

        getDataFromDatabase(patientId.toString(), patientName)

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
            }
        }
    }

    private fun getDataFromDatabase(pacientId: String, patientName: TextView) {
        val pacientRef = FirebaseDatabase.getInstance().getReference("Users").child(pacientId)
        pacientRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val patient = dataSnapshot.getValue(UserModel::class.java)
                if (patient != null) {
                    val patientFirstName = patient.firstName
                    val patientLastName = patient.lastName
                    Log.d("TAG", "First name: ${patientFirstName}, Last name: ${patientLastName}")

                    patientName.setText(patientFirstName + " " + patientLastName)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
    }
}