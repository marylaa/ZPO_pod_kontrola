package com.example.myapp.pills_list

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
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

class UserScheduleNextDayActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private var pillList: MutableList<PillModel> = mutableListOf()
    private var pillListCustom: MutableList<PillModelCustom> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_pills_schedule_next_day)

        val beforeDay = findViewById<ImageButton>(R.id.beforeDay)
        beforeDay.setOnClickListener(this)

        newRecyclerView = findViewById(R.id.rvPills)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)
        getDataFromDatabase()

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@UserScheduleNextDayActivity, UserScheduleActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_report -> {
                    val intent = Intent(this@UserScheduleNextDayActivity, MainActivityMonthlyReport::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_settings -> {
                    val intent = Intent(this@UserScheduleNextDayActivity, PatientSettingsActivity::class.java)
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
                };
                R.id.addRaport -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.beforeDay -> {
                    val intent = Intent(this, UserScheduleActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDataFromDatabase(): MutableList<PillModel> {
        dbRef = FirebaseDatabase.getInstance().getReference("Pills")
        val user = FirebaseAuth.getInstance().currentUser;
        val uid = user?.uid

        val current = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val tomorrow = current.plusDays(1).format(formatter)

        pillList = mutableListOf()
        pillListCustom = mutableListOf()

        val query = dbRef.orderByChild("pacient").equalTo(uid)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                pillList.clear()
                pillListCustom.clear()
                for (snapshot in dataSnapshot.children) {
                    try {
                        val pill = snapshot.getValue(PillModel::class.java)

                        if (pill?.frequency !== "Niestandardowa") {
                            // sprawdzenie czy jutro bedzie brana tabletka
                            if (pill!!.date_last.equals(tomorrow) || pill!!.date_next.equals(tomorrow)) {
                                pillList.add(pill!!)
                            }
                        }
                    } catch (e: Exception) {
                        // NIESTANDARDOWA częstotliwość

                        val days = arrayOf("Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela")
                        val today = LocalDate.now()
                        val dayOfWeek = today.dayOfWeek.value

                        val pill = snapshot.getValue(PillModelCustom::class.java)
                        val tomorrowDay = days.get(dayOfWeek)

                        for (item in pill?.time_list.orEmpty()) {
                            val dayValue = item["day"]

                            // sprawdzenie czy jutro bedzie brana tabletka
                            if (dayValue.toString().equals(tomorrowDay)) {
                                pillListCustom.add(pill!!)
                            }
                        }
                    }
                }

                val mergedList = mutableListOf<Any>()
                mergedList.addAll(pillList)
                mergedList.addAll(pillListCustom)
                newRecyclerView.adapter = PillNextDayItemAdapter(mergedList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
        return pillList
    }
}


