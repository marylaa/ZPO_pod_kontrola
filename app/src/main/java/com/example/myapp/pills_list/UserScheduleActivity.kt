package com.example.myapp.pills_list

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.monthly_report.MainActivityMonthlyReport
import com.example.myapp.report.MainActivity
import com.example.myapp.settings.PatientSettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class UserScheduleActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private var pillList: MutableList<PillModel> = mutableListOf()
    private var pillListCustom: MutableList<PillModelCustom> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_pills_schedule)

        val addButton = findViewById<Button>(R.id.addPill)
        addButton.setOnClickListener(this)

        val nextDay = findViewById<ImageButton>(R.id.nextDay)
        nextDay.setOnClickListener(this)

        val addReport = findViewById<Button>(R.id.addRaport)
        addReport.setOnClickListener(this)

        newRecyclerView = findViewById(R.id.rvPills)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)
        getDataFromDatabase()

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.menu.findItem(R.id.navigation_home).isChecked = true
        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    true
                }
                R.id.navigation_report -> {
                    val intent = Intent(this@UserScheduleActivity, MainActivityMonthlyReport::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_settings -> {
                    val intent = Intent(this@UserScheduleActivity, PatientSettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onClick(view: View?) {
        if(view !=null){
            when (view.id){
                R.id.addPill ->{
                    val intent = Intent(this, AddPillActivity::class.java)
                    startActivity(intent)
                }
                R.id.addRaport -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.nextDay -> {
                    val intent = Intent(this, UserScheduleNextDayActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDataFromDatabase(): MutableList<PillModel> {
        dbRef = FirebaseDatabase.getInstance().getReference("Pills")
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        val current = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val today = current.format(formatter)

        val query = dbRef.orderByChild("pacient").equalTo(uid)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                pillList.clear()
                pillListCustom.clear()
                for (snapshot in dataSnapshot.children) {
                    try {
                        val pill = snapshot.getValue(PillModel::class.java)

                        if (pill?.frequency !== "Niestandardowa") {
                            val dateBefore = LocalDate.parse(pill!!.date_last, formatter)

                            // przesuwanie dat, jesli zmienil sie dzien
                            if (dateBefore.isBefore(current)) {
                                val dateAfter = LocalDate.parse(pill!!.date_next, formatter)
                                pill!!.date_last = pill!!.date_next
                                val daysBetween = ChronoUnit.DAYS.between(dateBefore, dateAfter)
                                val newDate = dateAfter.plusDays(daysBetween)
                                pill!!.date_next = newDate.format(formatter)
                                // odcheckowanie checkboxów
                                pill.time_list!![0]?.set(1, false)
                                if (pill.time_list!!.size >= 2) {
                                    pill.time_list!![1]?.set(1, false)
                                }
                                if (pill.time_list!!.size === 3) {
                                    pill.time_list!![2]?.set(1, false)
                                }
                            }
                            dbRef.child(pill!!.id.toString()).setValue(pill)

                            // sprawdzenie czy dziś bedzie brana tabletka
                            if (pill!!.date_last.equals(today)) {
                                pillList.add(pill!!)
                            }
                        }
                    } catch (e: Exception) {
                        // NIESTANDARDOWA częstotliwość

                        val days = arrayOf("Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela")
                        val today = LocalDate.now()
                        val dayOfWeek = today.dayOfWeek.value - 1

                        val pill = snapshot.getValue(PillModelCustom::class.java)
                        val todayDay = days.get(dayOfWeek)

                        for (item in pill?.time_list.orEmpty()) {
                            val dayValue = item["day"]

                            Log.e("Dzień tygodnia", dayValue.toString())
                            // sprawdzenie czy dziś bedzie brana tabletka
                            if (dayValue.toString().equals(todayDay)) {
                                pillListCustom.add(pill!!)
                            }
                        }
                    }
                }

                val mergedList = mutableListOf<Any>()
                mergedList.addAll(pillList)
                mergedList.addAll(pillListCustom)
                newRecyclerView.adapter = PillItemAdapter(mergedList, this@UserScheduleActivity)
                sendNotificationHour()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
        return pillList
    }

    private fun sendNotificationHour() {
        for (pill in pillList) {
            if (pill.frequency !== "Niestandardowa") {
                for(time in pill.time_list!!) {
                    if (time?.get(1) as Boolean) {
                        break
                    }
                    val getTime = time[0].toString().split(":")

                    // Pobierz godzinę zażycia tabletki
                    val hour = getTime[0].toInt()
                    val minute = getTime[1].toInt()

                    // Utwórz kalendarz i ustaw datę i godzinę powiadomienia
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)

                    // Sprawdź, czy czas powiadomienia jeszcze nie minął
                    if (calendar.timeInMillis > System.currentTimeMillis()) {
                        // Ustaw harmonogram powiadomienia przy użyciu AlarmManagera
                        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val intent = Intent(this@UserScheduleActivity, NotificationReceiver::class.java)
                        intent.putExtra("pillName", pill.name)
                        val pendingIntent = PendingIntent.getBroadcast(
                            this@UserScheduleActivity,
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )

                        // Ustaw harmonogram powiadomienia
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    }
                }
            }
        }
    }
}

