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
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ViewPatientsActivity : AppCompatActivity() {

    private lateinit var newRecyclerView: RecyclerView
    private var patientList: MutableList<UserModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_doctor_page)

        newRecyclerView = findViewById(R.id.rvPacients)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)
        getDataFromDatabase().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                newRecyclerView.adapter = PatientItemAdapter(patientList)
                }
            }



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
                    val patientIds = patientList.map { it.id }.toTypedArray()
                    intent.putExtra("patientIds", patientIds)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun getDataFromDatabase(): Task<Unit> {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        val doctorRef = FirebaseDatabase.getInstance().getReference("Patients")
        val query = doctorRef.orderByChild("doctor").equalTo(uid)
        return query.get().continueWith { task ->
            if (task.isSuccessful) {
                patientList.clear()

                for (snapshot in task.result.children) {
                    val patientId = snapshot.child("patient").value.toString()
                    getPatientFromDatabase(patientId)
                }
            }
        }
    }

    private fun getPatientFromDatabase(patientId: String) {
        val pacientRef = FirebaseDatabase.getInstance().getReference("Users").child(patientId)
        pacientRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val patient = dataSnapshot.getValue(UserModel::class.java)
                if (patient != null) { // sprawdzamy czy pacjent nie jest pusty
                    patientList.add(patient)
                    newRecyclerView.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
    }
}