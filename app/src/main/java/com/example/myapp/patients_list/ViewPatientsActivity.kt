package com.example.myapp.patients_list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.settings.DoctorSettingsActivity
import com.example.myapp.R
import com.example.myapp.login.UserModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ViewPatientsActivity : AppCompatActivity() {

    private lateinit var newRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_doctor_page)

        newRecyclerView = findViewById(R.id.rvPacients)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)
        getDataFromDatabase()

        val navView: BottomNavigationView = findViewById(R.id.navigation_bar)
        navView.menu.findItem(R.id.navigation_home).isChecked = true

        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@ViewPatientsActivity, ViewPatientsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_settings -> {
                    val intent = Intent(this@ViewPatientsActivity, DoctorSettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun getDataFromDatabase() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        val patientList: MutableList<UserModel> = mutableListOf()

        val doctorRef = FirebaseDatabase.getInstance().getReference("Patients")
        val query = doctorRef.orderByChild("doctor").equalTo(uid)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                patientList.clear()

                for (snapshot in dataSnapshot.children) {
                    val patientId = snapshot.child("patient").value.toString()
                    getDataFromDatabase(patientId, patientList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
    }

    private fun getDataFromDatabase(pacientId: String, pacientList: MutableList<UserModel>) {
        val pacientRef = FirebaseDatabase.getInstance().getReference("Users").child(pacientId)
        pacientRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val pacient = dataSnapshot.getValue(UserModel::class.java)
                if (pacient != null) { // sprawdzamy czy pacjent nie jest pusty
                    pacientList.add(pacient)
                    newRecyclerView.adapter = PatientItemAdapter(pacientList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
    }
}