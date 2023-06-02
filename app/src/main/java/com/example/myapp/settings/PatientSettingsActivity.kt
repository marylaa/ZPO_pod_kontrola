package com.example.myapp.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.example.myapp.patient_notifications.MainNotifications
import com.example.myapp.R
import com.example.myapp.login.LoginActivity
import com.example.myapp.login.UserModel
import com.example.myapp.monthly_report.MainActivityMonthlyReport
import com.example.myapp.patient_notifications.NotificationModelAlert
import com.example.myapp.patients_list.PatientDoctorModel
import com.example.myapp.pills_list.PatientAllPillsActivity
import com.example.myapp.pills_list.UserScheduleActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class PatientSettingsActivity : AppCompatActivity(), View.OnClickListener {

    private var logoutButton: AppCompatImageButton? = null
    private lateinit var dbRef: DatabaseReference
    private lateinit var doctorsList: ArrayList<String>
    private lateinit var userId: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_patient)

        userId = FirebaseAuth.getInstance().currentUser!!.uid.toString()
        doctorsList = arrayListOf()

        logoutButton = findViewById(R.id.logoutButton)
        logoutButton?.setOnClickListener { logoutUser() }

        val showPills = findViewById<ImageButton>(R.id.showPills)
        showPills.setOnClickListener(this)

        val notifications = findViewById<ImageButton>(R.id.showNotifications)
        notifications.setOnClickListener(this)

        val healthAlertButton = findViewById<ImageButton>(R.id.healthAlertImage)
        healthAlertButton.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Potwierdzenie")
                .setMessage("Czy na pewno chcesz powiadomić lekarza o pogorszeniu się stanu Twojego zdrowia?")
                .setPositiveButton("Tak") { _, _ ->
                    getDoctorFromDatabase(userId).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            healthAlert()
                            Toast.makeText(this@PatientSettingsActivity, "Zgłoszenie zostało wysłane", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Anuluj") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            alertDialog.show()
        }


        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.menu.findItem(R.id.navigation_settings).isChecked = true
        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent =
                        Intent(this@PatientSettingsActivity, UserScheduleActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_report -> {
                    val intent =
                        Intent(this@PatientSettingsActivity, MainActivityMonthlyReport::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun logoutUser() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finishAffinity()
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.showPills -> {
                    val intent = Intent(this, PatientAllPillsActivity::class.java)
                    startActivity(intent)
                }
                R.id.showNotifications -> {
                    val intent = Intent(this, MainNotifications::class.java)
                    startActivity(intent)
                }

            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun healthAlert() {
        val dbFirebase = FirebaseDatabase.getInstance()
        val dbReference = dbFirebase.getReference()

        dbRef = FirebaseDatabase.getInstance().getReference("Notifications")

        var firstName = ""
        var lastName = ""

        val notificationsQuery = dbReference.child("Users")
            .orderByChild("id")
            .equalTo(userId)

        notificationsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    firstName = snapshot.child("firstName").getValue(String::class.java)!!
                    lastName = snapshot.child("lastName").getValue(String::class.java)!!
                    break
                }

                Log.d("LEKARZE", doctorsList.toString())
                val patientName = firstName + " " + lastName
                sendAlertToDoctor(patientName)

                for (doctor in doctorsList) {
                    val notificationsQuery =
                        dbReference.child("Users").orderByChild("id").equalTo(doctor)
                    notificationsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (snapshot in dataSnapshot.children) {
                                firstName = snapshot.child("firstName").getValue(String::class.java)!!
                                lastName = snapshot.child("lastName").getValue(String::class.java)!!

                                val id = UUID.randomUUID().toString()
                                val message = "Potwierdzenie, lekarz $firstName $lastName otrzymał zgłoszenie o Twoim złym stanie zdrowia."

                                val currentDate = LocalDate.now().toString()
                                var pillName = "Nie dotyczy"

                                val notification = NotificationModelAlert(
                                    message,
                                    pillName,
                                    currentDate,
                                    userId,
                                    id,
                                    false
                                )
                                dbRef.child(id).setValue(notification)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e("Error", databaseError.message)
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Error", databaseError.message)
            }
        })
    }

    private fun getDoctorFromDatabase(patientId: String): Task<Boolean> {
        var exists = false
        val pacientRef = FirebaseDatabase.getInstance().getReference("Patients").orderByChild("patient").equalTo(patientId)
        return pacientRef.get().continueWith { task ->
            Log.d("WCHODZI", task.exception.toString())
            if (task.isSuccessful) {
                Log.d("SUCC", doctorsList.toString())
                val snapshot = task.result.children
                for (snap in snapshot) {
                    val doctor = snap.getValue(PatientDoctorModel::class.java)
                    doctorsList.add(doctor!!.doctor)
                    Log.d("AAA", doctorsList.toString())
                    exists = true
                }
            }
            exists
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendAlertToDoctor(patientName: String) {
        dbRef = FirebaseDatabase.getInstance().getReference("Notifications")

        val message = "Uwaga, pacjent " + patientName + " zgłasza pogorszenie się stanu zdrowia! Sprawdź jego raport miesięczny."

        val currentDate = LocalDate.now().toString()
        var pillName = "Nie dotyczy"

        for (doctor in doctorsList) {
            val id = UUID.randomUUID().toString()

            val notification = NotificationModelAlert(
                message,
                pillName,
                currentDate,
                doctor,
                id,
                false
            )
            dbRef.child(id).setValue(notification)
        }
    }
}