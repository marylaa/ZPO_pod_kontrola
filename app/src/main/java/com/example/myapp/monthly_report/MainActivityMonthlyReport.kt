package com.example.myapp.monthly_report


//import kotlinx.android.synthetic.main.contact_monthly.*
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.databinding.ActivityMainMonthlyBinding
import com.example.myapp.pills_list.UserScheduleActivity
import com.example.myapp.report.Report
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.*


class MainActivityMonthlyReport : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var adapterDate: RecycleViewAdapter
    private lateinit var adapter: RecycleViewAdapterItem
    lateinit var linelist: ArrayList<Entry>
    lateinit var lineDataSet: LineDataSet
    lateinit var lineData: LineData
    private lateinit var binding: ActivityMainMonthlyBinding
    private lateinit var dbRef: DatabaseReference
    private val paths = arrayOf("Ciśnienie [mmHg]", "Aktywność [godz.]", "Waga [kg]","Sen [godz.]","Cukier [mmol/L]","Temp. ciała [oC]"  )


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_monthly)


//        binding = ActivityMainMonthlyBinding.inflate(layoutInflater)
//        setContentView(binding.root)

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
        val horizontalLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView1.layoutManager = horizontalLayoutManager
        adapterDate = RecycleViewAdapter(this, viewColors, dates)
        recyclerView1.adapter = adapterDate





//        var entries = getDataFromDatabase { data ->
//            createDict(data, "pressure")
//        }

        var spinner = findViewById<View>(R.id.spinner3) as Spinner
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this@MainActivityMonthlyReport,
            android.R.layout.simple_spinner_item, paths
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)
        spinner.setOnItemSelectedListener(this)

//        getDataFromDatabase { data ->
//            createDict(data, "weight")
//        }



        

//        linelist = ArrayList()
//        linelist.add(Entry(10f, 100f))
//        linelist.add(Entry(20f, 200f))
//        linelist.add(Entry(30f, 100f))
//        linelist.add(Entry(40f, 500f))




//        var keys = dict.keys
//        var values = dict.values.map { it.toFloat() }
//        var entries = keys.zip(values).map { (key, value) -> Entry(key, value) }
//
//        val lineDataSet = LineDataSet(entries, "count")
//        val lineData = LineData(lineDataSet)
//        val lineChart = findViewById<LineChart>(R.id.lineChart)
//        lineChart.data = lineData
//        lineDataSet.colors = ColorTemplate.JOYFUL_COLORS
//        lineDataSet.valueTextColor = Color.BLUE
//        lineDataSet.valueTextSize = 20f
//        lineChart.invalidate()
//        lineChart.invalidate()










        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
//        val navController = findNavController(R.id.navigation_home)
        navView.menu.findItem(R.id.navigation_dashboard).isChecked = true

        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent =
                        Intent(this@MainActivityMonthlyReport, UserScheduleActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_dashboard -> {
                    true
                }
                R.id.navigation_notifications -> {
                    val intent =
                        Intent(this@MainActivityMonthlyReport, UserScheduleActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

    }



    private fun getDataFromDatabase(callback: (List<String>) -> Unit): MutableList<String> {
        val dbRef = FirebaseDatabase.getInstance().getReference("report")
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        val query = dbRef.orderByChild("user").equalTo(uid)
        val reportValuesList = mutableListOf<String>()

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val reportValuesList = mutableListOf<String>()

                for (snapshot in dataSnapshot.children) {
                    for (rep in snapshot.children){
                        val reportValues = rep.getValue<String>().toString()
                        reportValuesList.add(reportValues)
                    }
                }

                Log.d("glowne zczytanie", reportValuesList.toString())

                // Zwracamy wartość przez callback
                callback(reportValuesList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
        return reportValuesList
    }


    //    private fun getDataFromDatabase(): MutableList<String> {
//        val dbRef = FirebaseDatabase.getInstance().getReference("report")
//        val user = FirebaseAuth.getInstance().currentUser
//        val uid = user?.uid
//
//        val query = dbRef.orderByChild("user").equalTo(uid)
//        val reportValuesList = mutableListOf<String>()
//
//        query.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val reports = mutableListOf<String>()
////                var inputsString = ""
////                val valueList = mutableListOf<String>()
//
//
//
//                for (snapshot in dataSnapshot.children) {
////                    val elem = mutableListOf<String>()
////                    val report = snapshot.getValue(Report::class.java)
////                    val date = report?.date ?: ""
////                    val notes = report?.notes ?: ""
////                    val mood = report?.mood?.toString() ?: ""
////
////                    elem.add(date)
////                    elem.add(notes)
////                    elem.add(mood)
//
//
//                    for (rep in snapshot.children){
//                        val reportValues = rep.getValue<String>().toString()
//                        reportValuesList.add(reportValues)
//                    }
//
////                    val inputsList = mutableListOf<String>()
////                    report?.valuesList?.forEach { value ->
////                        inputsList.add("${value.name}: ${value.input}")
////                    }
////                    inputsString = inputsList.joinToString(separator = ", ")
//
////                    reports.add("Notes: $notes, Mood: $mood, date: $date")
////                    reports.add(elem.toString())
//                }
//
//
//                Log.d("TAG", reports.toString())
//                Log.d("TAG", reportValuesList.toString())
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("TAG", "Błąd")
//            }
//        })
//
//        return reportValuesList
//    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun createDict(data: List<Any>, param: String): MutableList<Entry> {
        val dictionary = mapOf("Ciśnienie [mmHg]" to 0, "Aktywność [godz.]" to 1, "Waga [kg]" to 2, "Sen [godz.]" to 3, "Temp. ciała [oC]" to 4, "Cukier [mmol/L]" to 5, "mood" to 6, "notes" to 7)
        var index = dictionary[param]
        var resultDict = mutableMapOf<String, Float>()

//        val moodDict = mutableMapOf<String, String>()
//        val notesDict = mutableMapOf<String, String>()
//        val pressureDict = mutableMapOf<String, String>()
        for (i in data.indices step 10) {
            if (data[i] is String) {
                try {
                    var date = data[i+6] as String
                    var value = data[i + index!! + 1] as String
                    resultDict[date] = value.toFloat()

                } catch (e: IndexOutOfBoundsException) {
                    continue
                }
            }
        }
        val formatter = DateTimeFormatter.ofPattern("yyyy-M-d")
        val entries = resultDict.entries.map { entry ->
            val date = LocalDate.parse(entry.key, formatter)
            Entry(
                date.dayOfMonth.toFloat(),
                entry.value.toFloat()
            )
        }.sortedBy { it.x }.toMutableList()

        var label = ""

        if(param ===  "pressure"){
            label = "Ciśnienie [mmHg]"
        }else if(param === "workout"){
            label = "Aktywność [godz.]"
        }else if(param === "weight"){
            label = "Waga [kg]"
        }else if(param === "sleep"){
            label = "Sen [godz.]"
        }else if(param === "sugar"){
            label = "Cukier [mmol/L]"
        }else if(param === "temp"){
            label = "Temp. ciała [oC]"
        }

        val lineDataSet = LineDataSet(entries, param)
        val lineData = LineData(lineDataSet)
        val lineChart = findViewById<LineChart>(R.id.lineChart)
        lineChart.data = lineData

// Ustawienie ValueFormatter do etykiet osi X
        lineChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

// Ustawienie granulacji etykiet osi X
        lineChart.xAxis.granularity = 1f

        val colors = ColorTemplate.JOYFUL_COLORS.toList()
        lineDataSet.colors = colors
        lineDataSet.valueTextColor = Color.BLUE
        lineDataSet.valueTextSize = 20f
        lineChart.invalidate()


        Log.d("dic", resultDict.keys.toString())
        return entries


    }




    private fun getDataFromDatabase11() {
        val dbRef = FirebaseDatabase.getInstance().getReference("report")
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        val query = dbRef.orderByChild("user").equalTo(uid)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val reports = mutableListOf<Report>()

                for (snapshot in dataSnapshot.children) {
                    val report = snapshot.getValue(Report::class.java)
                    report?.let { reports.add(it) }
                }

                val valuesList = reports.firstOrNull()?.valuesList?.map { it.toString() }?.joinToString(", ")
                val date = reports.firstOrNull()?.date ?: ""
                val mood = reports.firstOrNull()?.mood ?: ""
                val notes = reports.firstOrNull()?.notes ?: ""
                val user = reports.firstOrNull()?.user ?: ""

                val result = "Value: $valuesList, date: $date, mood: $mood, notes: $notes, user: $user"
                Log.d("TAG", result)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = parent?.getItemAtPosition(position) as String

        getDataFromDatabase { data ->
            createDict(data, selectedItem)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Handle no item selection here
    }


}







