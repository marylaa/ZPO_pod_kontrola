package com.example.myapp.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.example.myapp.patients_list.DoctorAddPatientsActivity
import com.example.myapp.R
import com.example.myapp.login.LoginActivity
import com.example.myapp.notifications.MainNotificationsDoctor
import com.example.myapp.patients_list.ViewPatientsActivity
import com.facebook.login.LoginManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class DoctorSettingsActivity : AppCompatActivity() {

    private var logoutButton: AppCompatImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_doctor)

        val patientIds = intent.getStringArrayExtra("patientIds")

        logoutButton = findViewById(R.id.logoutButton)
        logoutButton?.setOnClickListener { logoutUser() }

        var addPacient = findViewById<TextView>(R.id.textPatient)
        addPacient.setOnClickListener {
            val intent = Intent(this@DoctorSettingsActivity, DoctorAddPatientsActivity::class.java)
            intent.putExtra("patientIds", patientIds)
            startActivity(intent)
        }

        var notifications = findViewById<TextView>(R.id.notifications)
        notifications.setOnClickListener {
            val intent = Intent(this@DoctorSettingsActivity, MainNotificationsDoctor::class.java)
            intent.putExtra("patientIds", patientIds)
            startActivity(intent)
        }

        var textLogout = findViewById<TextView>(R.id.textLogout)
        textLogout.setOnClickListener {
            logoutUser()
        }

        val messagesButton = findViewById<ImageButton>(R.id.showNotifications)
        messagesButton?.setOnClickListener {
            val intent = Intent(this@DoctorSettingsActivity, MainNotificationsDoctor::class.java)
            intent.putExtra("patientIds", patientIds)
            startActivity(intent)
        }

        val addPatientButton = findViewById<ImageButton>(R.id.patientButton)
        addPatientButton.setOnClickListener {
            val intent = Intent(this@DoctorSettingsActivity, DoctorAddPatientsActivity::class.java)
            intent.putExtra("patientIds", patientIds)
            startActivity(intent)
        }

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.menu.findItem(R.id.navigation_settings).isChecked = true
        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@DoctorSettingsActivity, ViewPatientsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun logoutUser() {
        // Wyloguj użytkownika z Facebooka
        LoginManager.getInstance().logOut()

        val auth = FirebaseAuth.getInstance()
        auth.signOut()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finishAffinity()
    }
}