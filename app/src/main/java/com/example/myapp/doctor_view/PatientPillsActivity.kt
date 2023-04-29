package com.example.myapp.doctor_view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.patients_list.ViewPatientsActivity
import com.example.myapp.pills_list.PillModel
import com.example.myapp.settings.DoctorSettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*


class PatientPillsActivity : AppCompatActivity(), View.OnClickListener {

    private var patientId: String? = null
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_pills_schedule)

        val backButton = findViewById<ImageButton>(R.id.back)
        backButton.setOnClickListener(this)

        val addButton = findViewById<Button>(R.id.addPill)
        addButton.setOnClickListener(this)

        patientId = intent.getStringExtra("patientId")
        newRecyclerView = findViewById(R.id.rvPatientPills)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)
        getDataFromDatabase()

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@PatientPillsActivity, ViewPatientsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_settings -> {
                    val intent =
                        Intent(this@PatientPillsActivity, DoctorSettingsActivity::class.java)
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
                    val intent = Intent(this, PatientActionsActivity::class.java)
                    intent.putExtra("patientId", patientId)
                    startActivity(intent)
                }

                R.id.addPill -> {
                    val intent = Intent(this, DoctorAddPillActivity::class.java)
                    intent.putExtra("patientId", patientId)
                    startActivity(intent)
                }
            }
        }
    }

    private fun getDataFromDatabase() {
        dbRef = FirebaseDatabase.getInstance().getReference("Pills")

        val pillList: MutableList<PillModel> = mutableListOf()

        val query = dbRef.orderByChild("pacient").equalTo(patientId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                pillList.clear()
                for (snapshot in dataSnapshot.children) {
                    val pill = snapshot.getValue(PillModel::class.java)
                    pillList.add(pill!!)
                }
                newRecyclerView.adapter = PatientPillItemAdapter(pillList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
    }
}