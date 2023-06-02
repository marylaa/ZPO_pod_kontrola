package com.example.myapp.patient_notifications

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.monthly_report.MainActivityMonthlyReport
import com.example.myapp.patients_list.ViewPatientsActivity
import com.example.myapp.pills_list.UserScheduleActivity
import com.example.myapp.settings.DoctorSettingsActivity
import com.example.myapp.settings.PatientSettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainNotificationsDoctor : AppCompatActivity(), View.OnClickListener {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_notifications_doctor)

        newRecyclerView = findViewById(R.id.rvNotifications)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)
        getDataFromDatabase()

        var back = findViewById<ImageButton>(R.id.back)
        back.setOnClickListener(this)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@MainNotificationsDoctor, ViewPatientsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_settings -> {
                    val intent = Intent(this@MainNotificationsDoctor, DoctorSettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun getDataFromDatabase() {
        dbRef = FirebaseDatabase.getInstance().getReference("Notifications")
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        val messagesList: MutableList<NotificationModelAlert> = mutableListOf()

        val query = dbRef.orderByChild("recipient").equalTo(userId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messagesList.clear()
                for (snapshot in dataSnapshot.children) {
                    val notification = snapshot.getValue(NotificationModelAlert::class.java)
                    messagesList.add(notification!!)
                }
                newRecyclerView.adapter = NotificationsAdapter(messagesList, this@MainNotificationsDoctor)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.back -> {
                    val intent = Intent(this, DoctorSettingsActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}