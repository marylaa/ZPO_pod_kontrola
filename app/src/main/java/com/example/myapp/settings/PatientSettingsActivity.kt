package com.example.myapp.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.example.myapp.MainNotifications
import com.example.myapp.R
import com.example.myapp.login.LoginActivity
import com.example.myapp.pills_list.PatientAllPillsActivity
import com.example.myapp.pills_list.UserScheduleActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class PatientSettingsActivity : AppCompatActivity(), View.OnClickListener {

    private var logoutButton: AppCompatImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_patient)

        logoutButton = findViewById(R.id.logoutButton)
        logoutButton?.setOnClickListener { logoutUser() }

        val showPills = findViewById<ImageButton>(R.id.showPills)
        showPills.setOnClickListener(this)

        val notifications = findViewById<TextView>(R.id.notifications)
        notifications.setOnClickListener(this)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.menu.findItem(R.id.navigation_settings).isChecked = true

        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@PatientSettingsActivity, UserScheduleActivity::class.java)
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
                R.id.notifications -> {
                    val intent = Intent(this, MainNotifications::class.java)
                    startActivity(intent)
                }
            }
        }
    }

}