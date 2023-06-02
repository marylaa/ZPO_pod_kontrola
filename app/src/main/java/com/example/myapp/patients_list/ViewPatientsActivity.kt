package com.example.myapp.patients_list

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.settings.DoctorSettingsActivity
import com.example.myapp.R
import com.example.myapp.login.UserModel
import com.example.myapp.patient_notifications.NotificationModelAlert
import com.example.myapp.pills_list.PatientAllPillItemAdapter
import com.example.myapp.pills_list.PillModel
import com.example.myapp.pills_list.UserScheduleActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ViewPatientsActivity : AppCompatActivity() {

    private lateinit var newRecyclerView: RecyclerView
    private var patientList: MutableList<UserModel> = mutableListOf()
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_doctor_page)

        userId = FirebaseAuth.getInstance().currentUser!!.uid
        getNotificationsFromDatabase()

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
        val doctorRef = FirebaseDatabase.getInstance().getReference("Patients")
        val query = doctorRef.orderByChild("doctor").equalTo(userId)
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

    private fun getNotificationsFromDatabase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Notifications").orderByChild("recipient").equalTo(userId)
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val notification = snapshot.getValue(NotificationModelAlert::class.java)
                    if (notification!!.seen === false) {
                        sendNotification(notification)
                        updateMessageInDatabse(notification)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
    }

    fun updateMessageInDatabse(notification: NotificationModelAlert?) {
        val database = FirebaseDatabase.getInstance().getReference("Notifications")
        val updateData = hashMapOf<String, Any>("seen" to true)

        database.child(notification!!.id).updateChildren(updateData)
    }

    private fun sendNotification(notification: NotificationModelAlert?) {
        val intent = Intent(this@ViewPatientsActivity, UserScheduleActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this@ViewPatientsActivity,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        var messageBody = notification!!.message
        val channelId = "My channel ID"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this@ViewPatientsActivity, channelId)
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setContentTitle("Powiadomienie")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            this@ViewPatientsActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT,)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}