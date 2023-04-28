package com.example.myapp.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.example.myapp.R
import com.example.myapp.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth


class DoctorSettingsActivity : AppCompatActivity() {

    private var logoutButton: AppCompatImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_doctor)

        logoutButton = findViewById(R.id.logoutButton)
        logoutButton?.setOnClickListener { logoutUser() }
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