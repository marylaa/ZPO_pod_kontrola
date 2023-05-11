package com.example.myapp.settings

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.example.myapp.patients_list.DoctorAddPatientsActivity
import com.example.myapp.R
import com.example.myapp.login.LoginActivity
import com.example.myapp.patients_list.ViewPatientsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class DoctorSettingsActivity : AppCompatActivity() {

    private var logoutButton: AppCompatImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_doctor)

        val patientIds = intent.getStringArrayExtra("patientIds")

        logoutButton = findViewById(R.id.logoutButton)
        logoutButton?.setOnClickListener { logoutUser() }

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
        val auth = FirebaseAuth.getInstance()
        auth.signOut()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finishAffinity()
    }
}