package com.example.myapp.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.myapp.patient_notifications.MainNotifications
import com.example.myapp.R
import com.example.myapp.login.LoginActivity
import com.example.myapp.monthly_report.MainActivityMonthlyReport
import com.example.myapp.pills_list.NotificationModelAlert
import com.example.myapp.pills_list.PatientAllPillsActivity
import com.example.myapp.pills_list.UserScheduleActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDate
import java.util.*


class PatientSettingsActivity : AppCompatActivity(), View.OnClickListener {

    private var logoutButton: AppCompatImageButton? = null
    private lateinit var dbRef: DatabaseReference


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_patient)

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
                .setMessage("Czy na pewno chcesz powiadomić lekarza o pogorszeniu się stanu Twojego zdrowia ?")
                .setPositiveButton("Tak") { _, _ ->
                    healthAlert()
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
                    val intent = Intent(this@PatientSettingsActivity, UserScheduleActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_report -> {
                    val intent = Intent(this@PatientSettingsActivity, MainActivityMonthlyReport::class.java)
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

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid


        val id = UUID.randomUUID().toString()

        var firstName = ""
        var lastName = ""

        val notificationsQuery = dbReference.child("Users")
            .orderByChild("id")
            .equalTo(uid)

        notificationsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    firstName = snapshot.child("firstName").getValue(String::class.java)!!
                    lastName = snapshot.child("lastName").getValue(String::class.java)!!
                    break
                }

                val message = "Uwaga, pacjent " + firstName + " " + lastName + " zgłasza pogorszenie się stanu zdrowia!"

                val currentDate = LocalDate.now().toString()

                var pillName = "Nie dotyczy"

                // Wyślij powiadomienie
                val notification = NotificationModelAlert(
                    message,
                    pillName,
                    currentDate,
                    uid.toString(),
                    id
                )

                dbRef.child(id).setValue(notification)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("git", "git")
                        } else {
                            val exception = task.exception
                            Log.e("Error", exception?.message ?: "Unknown error")
                        }
                    }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Error", databaseError.message)
            }
        })
    }


}