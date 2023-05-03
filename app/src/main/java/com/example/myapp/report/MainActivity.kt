package com.example.myapp.report


//import com.example.myapp.report.databinding.ActivityMainBinding
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.monthly_report.MainActivityMonthlyReport
import com.example.myapp.pills_list.UserScheduleActivity
import com.example.myapp.settings.PatientSettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity(), View.OnClickListener {



    var report: Report = Report()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener(this)

        var value: Value? = null
        val valuesName =
            arrayOf("Ciśnienie krwii", "Aktywność", "Waga", "Sen", "Temp. ciała", "Poziom cukru")
        val valuesUnit = arrayOf("mmHg", "godz.", "kg", "godz.", "°C", "mmol/L")
        val valuesList = arrayListOf<Value>()

        for (i in valuesName.indices) {
            value = Value(valuesName[i], valuesUnit[i], i, " ")
            Log.d(TAG, valuesName[i])
            valuesList.add(value)
        }


        var adapter = ContactsAdapter(valuesList)




        // ...
        // Lookup the recyclerview in activity layout
        val rvContacts = findViewById<View>(R.id.rvItems) as RecyclerView
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


        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        var checkedRadioButton: String? = null

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            checkedRadioButton = radioButton.text.toString()
            this.report.setMood(checkedRadioButton)
        }


        val notes = findViewById<EditText>(R.id.contact_name6)
        notes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                val stringNotes = editable.toString()
                report.setNotes(stringNotes)
            }
        })


//        val stringNotes = notes.text.toString()
//            val report = Report(adapter.returnValuesArray(), checkedRadioButton, stringNotes)
        this.report.setValueList(adapter.returnValuesArray())
//        this.report.setNotes(stringNotes)

        Log.d(TAG, report.printReport())
        Log.d(TAG, report.printReport())
//            report.saveToFirebase()

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
//        val navController = findNavController(R.id.navigation_home)
        navView.menu.findItem(R.id.navigation_home).isChecked = true

        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@MainActivity, UserScheduleActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_report -> {
                    val intent = Intent(this@MainActivity, MainActivityMonthlyReport::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_settings -> {
                    val intent = Intent(this@MainActivity, PatientSettingsActivity::class.java)
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
                R.id.button -> {
                    // Create a new intent for the new activity
                    val intent = Intent(this, MainActivityMonthlyReport::class.java)


                    // Save the report to Firebase
                    this.report.saveToFirebase()

                    // Start the new activity
                    startActivity(intent)
                }

            }
        }else if(view != null && this.report.getMood() != ""){
            when (view.id) {
                R.id.button -> {
                    // Create a new intent for the new activity
                    val intent = Intent(this, UserScheduleActivity::class.java)


                    // Start the new activity
                    startActivity(intent)
                }
            }

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

//    override fun onClick(view: View?) {
//
//        if (view != null) {
//            when (view.id) {
//                R.id.button -> {
//                    // Create a new intent for the new activity
////                    val intent = Intent(this, UserScheduleActivity::class.java)
//
//
//                    // Save the report to Firebase
//                    this.report.saveToFirebase()
//
//                    // Start the new activity
////                    startActivity(intent)
//                }
//            }
//        }
//    }





}




