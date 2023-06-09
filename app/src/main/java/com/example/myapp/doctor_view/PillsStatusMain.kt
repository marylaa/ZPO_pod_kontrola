package com.example.myapp.doctor_view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.SharedObject
import com.example.myapp.patients_list.ViewPatientsActivity
import com.example.myapp.settings.DoctorSettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class PillsStatusMain : AppCompatActivity(), View.OnClickListener {

    private var patientId: String? = null


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.pills_status)

        patientId = intent.getStringExtra("patientId")




        var pillStatusList = SharedObject.getSortedDates()


        var adapter = PillsStatusAdapter(pillStatusList)



        Log.d("shared",SharedObject.getWantedPill())

        val pillNameTextView = findViewById<TextView>(R.id.pillName)
        pillNameTextView.text = SharedObject.getWantedPill()


        val backButton = findViewById<ImageButton>(R.id.back)
        backButton.setOnClickListener(this)




        // ...
        // Lookup the recyclerview in activity layout
        val rvContacts = findViewById<View>(R.id.rV) as RecyclerView
//
//        rvContacts.setAdapter(adapter);
//        adapter.notifyDataSetChanged()
        // Initialize contacts
//        contacts = Contact.createContactsList()
        // Create adapter passing in the sample user data
//        val adapter = ContactsAdapter(contacts)
        // Attach the adapter to the recyclerview to populate items
        rvContacts.adapter = adapter
        // Set layout manager to position the items
        rvContacts.layoutManager = LinearLayoutManager(this)


        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@PillsStatusMain, ViewPatientsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_settings -> {
                    val intent = Intent(this@PillsStatusMain, DoctorSettingsActivity::class.java)
                    intent.putExtra("patientIds", SharedObject.getlistPacientIds())
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }





    }


    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {

                R.id.back -> {
                    val intent = Intent(this, MonthyReportDoctor::class.java)
                    intent.putExtra("patientId", SharedObject.getPacientId())
                    startActivity(intent)
                }

            }
        }
    }



}