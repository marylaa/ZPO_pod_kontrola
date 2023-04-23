package com.example.myapp.report


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.pills_list.AddPillActivity
//import com.example.myapp.report.databinding.ActivityMainBinding
import com.example.myapp.report.ContactsAdapter



abstract class MainActivity : AppCompatActivity(), View.OnClickListener {


    var value: Value? = null
    val valuesName =
        arrayOf("Ciśnienie krwii", "Aktywność", "Waga", "Sen", "Temp. ciała", "Poziom cukru")
    val valuesUnit = arrayOf("mmHg", "godz.", "kg", "godz.", "°C", "mmol/L")
    var valuesList = arrayListOf<Value>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

//        val valuesList = arrayListOf<Value>()

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener(this)


        for (i in valuesName.indices) {
            val value = Value(valuesName[i], valuesUnit[i], i, " ")
            Log.d(TAG, valuesName[i])
            this.valuesList.add(value)
        }


        val adapter = ContactsAdapter(valuesList)


        // ...
        // Lookup the recyclerview in activity layout
        val rvContacts = findViewById<View>(R.id.rvItems) as RecyclerView
        // Initialize contacts
//        contacts = Contact.createContactsList()
        // Create adapter passing in the sample user data
//        val adapter = ContactsAdapter(contacts)
        // Attach the adapter to the recyclerview to populate items
        rvContacts.adapter = adapter
        // Set layout manager to position the items
        rvContacts.layoutManager = LinearLayoutManager(this)


        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        var checkedRadioButton: String? = null

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            checkedRadioButton = radioButton.text.toString()
        }


        val notes = findViewById<EditText>(R.id.contact_name6)
        button.setOnClickListener {
            val stringNotes = notes.text.toString()
            val report = Report(adapter.returnValuesArray(), checkedRadioButton, stringNotes)
            Log.d(TAG, report.printReport())
            report.saveToFirebase()


        }


    }

//    override fun onClick(view: View?) {
////        val stringNotes = notes.text.toString()
////        val report = Report(adapter.returnValuesArray(), checkedRadioButton, stringNotes)
////        Log.d(TAG, report.printReport())
////        report.saveToFirebase()
//
//        if (view != null) {
//            when (view.id) {
//
//                R.id.button -> {
//                    val intent = Intent(this, AddPillActivity::class.java)
//                    startActivity(intent)
//                }
//            }
//        }
//
//
//    }

    override fun onClick(view: View?) {

        val adapter = ContactsAdapter(valuesList)


        if (view != null) {
            when (view.id) {
                R.id.button -> {
                    // Create a new intent for the new activity
                    val intent = Intent(this, NewActivity::class.java)

                    // Create a new report
                    val report = Report(adapter.returnValuesArray(), checkedRadioButton, notes.text.toString())

                    // Save the report to Firebase
                    report.saveToFirebase()

                    // Start the new activity
                    startActivity(intent)
                }
            }
        }
    }





}




