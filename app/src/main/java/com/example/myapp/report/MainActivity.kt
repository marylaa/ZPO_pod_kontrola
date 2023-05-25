package com.example.myapp.report


//import com.example.myapp.report.databinding.ActivityMainBinding
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.EmptyActivity
import com.example.myapp.R
import com.example.myapp.monthly_report.MainActivityMonthlyReport
import com.example.myapp.pills_list.UserScheduleActivity
import com.example.myapp.settings.PatientSettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class MainActivity : AppCompatActivity(), View.OnClickListener {


    private var dataExist: Boolean = false
    var report: Report = Report()
    val valuesList = arrayListOf<Value>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener(this)

        var value: Value? = null
        val valuesName =
            arrayOf("Ciśnienie krwii", "Aktywność", "Waga", "Sen", "Temp. ciała", "Poziom cukru")
        val valuesUnit = arrayOf("mmHg", "godz.", "kg", "godz.", "°C", "mmol/L")


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



//        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
////        val navController = findNavController(R.id.navigation_home)
//        navView.menu.findItem(R.id.navigation_home).isChecked = true
//
//
//        navView.setOnNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.navigation_home -> {
//                    val intent = Intent(this@MainActivity, UserScheduleActivity::class.java)
//                    startActivity(intent)
//                    true
//                }
//                R.id.navigation_report -> {
//                    val intent = Intent(this@MainActivity, MainActivityMonthlyReport::class.java)
//                    startActivity(intent)
//                    true
//                }
//                R.id.navigation_settings -> {
//                    val intent = Intent(this@MainActivity, PatientSettingsActivity::class.java)
//                    startActivity(intent)
//                    true
//                }
//                else -> false
//            }
//        }

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)

        isCurrentUserPatientForPath { result ->
            dataExist = result
            Log.d("wynik", dataExist.toString())

//            // Sprawdź, czy użytkownik nie jest pacjentem
//            if (!dataExist) {
//                navView.menu.findItem(R.id.navigation_report).isChecked = false
//                val intent = Intent(this@MainActivity, EmptyActivity::class.java)
//                startActivity(intent)
//            }
        }

        navView.menu.findItem(R.id.navigation_home).isChecked = true

        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@MainActivity, UserScheduleActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_report -> {
                    // Dodatkowa logika, jeśli użytkownik nie jest pacjentem
//                    if (!dataExist) {
//                        val intent = Intent(this@MainActivity, EmptyActivity::class.java)
//                        startActivity(intent)
//                        true
//                    } else {
                        val intent = Intent(this@MainActivity, MainActivityMonthlyReport::class.java)
                        startActivity(intent)
                        true
//                    }
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

                    if (valuesList.size == 6 && report.getMood() != "") {

                        // Create a new intent for the new activity
                        val intent = Intent(this, MainActivityMonthlyReport::class.java)


                        // Save the report to Firebase
                        this.report.saveToFirebase()

                        // Start the new activity
                        startActivity(intent)

                    } else {
                        // Lista nie ma 6 elementów, wyświetl komunikat o błędzie lub wykonaj odpowiednie działania
                        Toast.makeText(this@MainActivity, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show()
                    }
//                    // Create a new intent for the new activity
//                    val intent = Intent(this, MainActivityMonthlyReport::class.java)
//
//
//                    // Save the report to Firebase
//                    this.report.saveToFirebase()
//
//                    // Start the new activity
//                    startActivity(intent)
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


    fun isCurrentUserPatientForPath(callback: (Boolean) -> Unit) {
        val dbRefReport = FirebaseDatabase.getInstance().getReference("report")
        val dbRefPills = FirebaseDatabase.getInstance().getReference("Pills")
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        val queryReport = dbRefReport.orderByChild("user").equalTo(uid)
        val queryPills = dbRefPills.orderByChild("user").equalTo(uid)

        queryReport.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val isPatientForReport = dataSnapshot.exists()
                if (isPatientForReport) {
                    callback.invoke(true)
                } else {
                    queryPills.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val isPatientForPills = dataSnapshot.exists()
                            callback.invoke(isPatientForPills)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Obsługa błędu odczytu danych z bazy Firebase
                            callback.invoke(false)
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Obsługa błędu odczytu danych z bazy Firebase
                callback.invoke(false)
            }
        })
    }








}




