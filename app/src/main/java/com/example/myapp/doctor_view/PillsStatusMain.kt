package com.example.myapp.doctor_view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.SharedObject

class PillsStatusMain : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.pills_status)



        var pillStatusList = SharedObject.getSortedDates()


        var adapter = PillsStatusAdapter(pillStatusList)



        Log.d("shared",SharedObject.getWantedPill())

        val pillNameTextView = findViewById<TextView>(R.id.pillName)
        pillNameTextView.text = SharedObject.getWantedPill()




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








    }
}