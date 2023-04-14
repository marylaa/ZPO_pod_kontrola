package com.example.myapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class UserScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_pills_schedule)

        val uID = intent
        val userID = uID.getStringExtra("uID")
    }
}


