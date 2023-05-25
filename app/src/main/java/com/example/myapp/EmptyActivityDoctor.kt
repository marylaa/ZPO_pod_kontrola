package com.example.myapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.example.myapp.patients_list.ViewPatientsActivity
import com.example.myapp.pills_list.UserScheduleActivity
import com.example.myapp.settings.PatientSettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class EmptyActivityDoctor : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.empty_activity_doctor)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)


        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@EmptyActivityDoctor, ViewPatientsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}