package com.example.myapp.monthly_report
import android.content.Intent
import com.example.myapp.monthly_report.RecycleViewAdapter
import com.example.myapp.monthly_report.RecycleViewAdapterItem



import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.pills_list.UserScheduleActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate

class MainActivityMonthlyReport : AppCompatActivity() {

    private lateinit var adapterDate: RecycleViewAdapter
    private lateinit var adapter: RecycleViewAdapterItem

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_monthly)

        // data to populate the RecyclerView with
        val viewColors = arrayListOf(
            Color.BLUE,
            Color.YELLOW,
            Color.MAGENTA
        )

        val dates = arrayListOf(
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(2),
            LocalDate.now().plusDays(3)
        )

        // set up the RecyclerView
        val recyclerView1: RecyclerView = findViewById(R.id.rvAnimals)
        val horizontalLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView1.layoutManager = horizontalLayoutManager
        adapterDate = RecycleViewAdapter(this, viewColors, dates)
        recyclerView1.adapter = adapterDate

        // data to populate the RecyclerView with
        val date = arrayListOf(
            "13:00",
            "16:30"
        )

        val doctors = arrayListOf(
            "lek.med. Jan Kowalski",
            "lek.med Anna Maria"
        )

        // set up the RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.rvItems)
        val verticalLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = verticalLayoutManager
        adapter = RecycleViewAdapterItem(this, date, doctors)
        recyclerView.adapter = adapter

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
//        val navController = findNavController(R.id.navigation_home)
        navView.menu.findItem(R.id.navigation_dashboard).isChecked = true

        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@MainActivityMonthlyReport, UserScheduleActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_dashboard -> {
                    true
                }
                R.id.navigation_notifications -> {
                    val intent = Intent(this@MainActivityMonthlyReport, UserScheduleActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

    }
}