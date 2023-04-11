package com.example.myapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    private var welcomeTextView: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val uID = intent
        val userID = uID.getStringExtra("uID")

        welcomeTextView = findViewById(R.id.welcomeText)
        welcomeTextView?.text = "Welcome ${userID}!";


    }

}


