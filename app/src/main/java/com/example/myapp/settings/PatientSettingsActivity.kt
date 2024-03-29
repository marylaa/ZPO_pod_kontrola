package com.example.myapp.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.example.myapp.notifications.MainNotifications
import com.example.myapp.R
import com.example.myapp.chat.ChatActivity
import com.example.myapp.login.LoginActivity
import com.example.myapp.monthly_report.MainActivityMonthlyReport
import com.example.myapp.notifications.NotificationModelAlert
import com.example.myapp.patients_list.PatientDoctorModel
import com.example.myapp.patients_list.PatientItemAdapter
import com.example.myapp.pills_list.PatientAllPillsActivity
import com.example.myapp.pills_list.UserScheduleActivity
import com.facebook.login.LoginManager
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class PatientSettingsActivity : AppCompatActivity(), View.OnClickListener {

    private var logoutButton: AppCompatImageButton? = null
    private lateinit var dbRef: DatabaseReference
    private lateinit var doctorsList: ArrayList<String>
    private lateinit var userId: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_patient)

        userId = FirebaseAuth.getInstance().currentUser!!.uid.toString()
        doctorsList = arrayListOf()

        getDoctorFromDatabase(userId)

        logoutButton = findViewById(R.id.logoutButton)
        logoutButton?.setOnClickListener { logoutUser() }

        var textLogout = findViewById<TextView>(R.id.textLogout)
        textLogout?.setOnClickListener { logoutUser() }

        val showPills = findViewById<ImageButton>(R.id.showPills)
        showPills.setOnClickListener(this)

        val showNotifications = findViewById<ImageButton>(R.id.showNotifications)
        showNotifications.setOnClickListener(this)

        val chatButton = findViewById<ImageButton>(R.id.choosedChat)
        chatButton.setOnClickListener(this)

        val pacientPills = findViewById<TextView>(R.id.pacientPills)
        pacientPills.setOnClickListener(this)

        val notifications = findViewById<TextView>(R.id.notifications)
        notifications.setOnClickListener(this)

        val chat = findViewById<TextView>(R.id.chat)
        chat.setOnClickListener(this)

        val healthAlertButton = findViewById<ImageButton>(R.id.healthAlertImage)
        healthAlertButton.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Potwierdzenie")
                .setMessage("Czy na pewno chcesz powiadomić lekarza o pogorszeniu się stanu Twojego zdrowia?")
                .setPositiveButton("Tak") { _, _ ->
                    getDoctorFromDatabase(userId).addOnCompleteListener { task ->
                        if(doctorsList.isNotEmpty()) {
                            if (task.isSuccessful) {
                                healthAlert { success ->
                                    if (success) {
                                        runOnUiThread {
                                            Toast.makeText(
                                                this@PatientSettingsActivity,
                                                "Zgłoszenie zostało wysłane",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        runOnUiThread {
                                            Toast.makeText(
                                                this@PatientSettingsActivity,
                                                "Wystąpił błąd",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                        }else{
                            val alertDialog = AlertDialog.Builder(this)
                                .setTitle("Brak lekarza")
                                .setMessage("Nie jesteś przypisany do żadnego lekarza. Zgłoszenie jest niemożliwe do wysłania")
                                .setPositiveButton("OK") { dialog, _ ->
                                    // Tutaj umieść kod, który ma być wykonany po naciśnięciu przycisku "OK"
                                    dialog.dismiss() // Zamyka okno dialogowe
                                }
                                .create()

                            alertDialog.show()

                        }
                    }
                }
                .setNegativeButton("Anuluj") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            alertDialog.show()
        }


        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.menu.findItem(R.id.navigation_settings).isChecked = true
        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent =
                        Intent(this@PatientSettingsActivity, UserScheduleActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_report -> {
                    val intent =
                        Intent(this@PatientSettingsActivity, MainActivityMonthlyReport::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun logoutUser() {
        // Wyloguj użytkownika z Facebooka
        LoginManager.getInstance().logOut()

        val auth = FirebaseAuth.getInstance()
        auth.signOut()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finishAffinity()
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.showPills -> {
                    val intent = Intent(this, PatientAllPillsActivity::class.java)
                    startActivity(intent)
                }
                R.id.pacientPills -> {
                    val intent = Intent(this, PatientAllPillsActivity::class.java)
                    startActivity(intent)
                }
                R.id.showNotifications -> {
                    val intent = Intent(this, MainNotifications::class.java)
                    startActivity(intent)
                }
                R.id.notifications -> {
                    val intent = Intent(this, MainNotifications::class.java)
                    startActivity(intent)
                }
                R.id.choosedChat ->{
                    if(doctorsList.isNotEmpty()) {
                        val intent = Intent(this, ChatActivity::class.java)
                        intent.putExtra("Id", doctorsList[0])
                        startActivity(intent)
                    }else{
                        val alertDialog = AlertDialog.Builder(this)
                            .setTitle("Brak lekarza")
                            .setMessage("Nie jesteś przypisany do żadnego lekarza. Kontakt jest niemożliwy")
                            .setPositiveButton("OK") { dialog, _ ->
                                // Tutaj umieść kod, który ma być wykonany po naciśnięciu przycisku "OK"
                                dialog.dismiss() // Zamyka okno dialogowe
                            }
                            .create()

                        alertDialog.show()

                    }
                }
                R.id.chat -> {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("Id", doctorsList[0])
                    startActivity(intent)
                }

            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun healthAlert(completion: (Boolean) -> Unit) {
        val dbFirebase = FirebaseDatabase.getInstance()
        val dbReference = dbFirebase.getReference()

        println("Health alert")

        dbRef = FirebaseDatabase.getInstance().getReference("Notifications")

        var firstName = ""
        var lastName = ""

        val notificationsQuery = dbReference.child("Users")
            .orderByChild("id")
            .equalTo(userId)

        notificationsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        firstName = snapshot.child("firstName").getValue(String::class.java)!!
                        lastName = snapshot.child("lastName").getValue(String::class.java)!!
                        break
                    }

                    Log.d("LEKARZE", doctorsList.toString())
                    val patientName = "$firstName $lastName"
                    sendAlertToDoctor(patientName)
                } else {
                    Log.d("Health Alert", "No matching user found.")
                    completion(false)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Error", "Health Alert query cancelled: ${databaseError.message}")
                completion(false)
            }
        })
    }

    private fun getDoctorFromDatabase(patientId: String): Task<Boolean> {
        var exists = false
        val pacientRef = FirebaseDatabase.getInstance().getReference("Patients").orderByChild("patient").equalTo(patientId)
        return pacientRef.get().continueWith { task ->
            if (task.isSuccessful) {
                val snapshot = task.result.children
                for (snap in snapshot) {
                    val doctor = snap.getValue(PatientDoctorModel::class.java)
                    val doctorId = doctor?.doctor

                    if (doctorId != null && !doctorsList.contains(doctorId)) {
                        doctorsList.add(doctorId)
                        exists = true
                    }
                }
            }
            exists
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendAlertToDoctor(patientName: String) {
        dbRef = FirebaseDatabase.getInstance().getReference("Notifications")

        val message = "Uwaga, pacjent " + patientName + " zgłasza pogorszenie się stanu zdrowia! Sprawdź jego raport miesięczny."

        val currentDate = LocalDate.now().toString()
        var pillName = "Nie dotyczy"

        println(doctorsList.toString())
        for (doctor in doctorsList) {
            val id = UUID.randomUUID().toString()

            val notification = NotificationModelAlert(
                message,
                pillName,
                currentDate,
                doctor,
                id,
                false,
                userId
            )
            dbRef.child(id).setValue(notification)
        }
    }


}