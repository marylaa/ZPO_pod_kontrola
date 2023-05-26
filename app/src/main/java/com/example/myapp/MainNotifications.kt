package com.example.myapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.monthly_report.MainActivityMonthlyReport
import com.example.myapp.pills_list.PillModel
import com.example.myapp.pills_list.UserScheduleActivity
import com.example.myapp.settings.PatientSettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainNotifications : AppCompatActivity(), View.OnClickListener {

    private var notificationList = mutableListOf<NotificationModel>()
    private lateinit var dbRef: DatabaseReference


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_notifications)

        getDataFromDatabase()

        val adapter = NotificationsAdapter(notificationList)

        val rvNotifications = findViewById<View>(R.id.rvNotifications) as RecyclerView

        var back = findViewById<ImageButton>(R.id.back)
        back.setOnClickListener(this)

        rvNotifications.adapter = adapter
        rvNotifications.layoutManager = LinearLayoutManager(this)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.menu.findItem(R.id.navigation_settings).isChecked = true

        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@MainNotifications, UserScheduleActivity::class.java)
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

        val pillList: MutableList<PillModel> = mutableListOf()

        val query = dbRef.orderByChild("pacient").equalTo(userId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                pillList.clear()
                for (snapshot in dataSnapshot.children) {
                    val notification = snapshot.getValue(NotificationModel::class.java)
                    Log.d("notification", notification.toString())
                    notificationList.add(notification!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
    }

    override fun onClick(view: View?) {

        if (view != null) {
            when (view.id) {
                R.id.button -> {
                    val intent = Intent(this, PatientSettingsActivity::class.java)

                    // Start the new activity
                    startActivity(intent)
                }
            }
        }
    }

}