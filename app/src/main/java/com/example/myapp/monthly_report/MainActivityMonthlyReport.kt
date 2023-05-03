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
import com.example.myapp.settings.PatientSettingsActivity
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
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
            Color.MAGENTA,
            Color.YELLOW,
            Color.MAGENTA
        )

//        val dates = arrayListOf(
//            LocalDate.now().minusDays(2),
//            LocalDate.now().minusDays(1),
//            LocalDate.now(),
//            LocalDate.now().plusDays(1),
//            LocalDate.now().plusDays(2),
//
//        )
//
//        // set up the RecyclerView
//        val recyclerView1: RecyclerView = findViewById(R.id.rvAnimals)
//        val horizontalLayoutManager =
//            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        recyclerView1.layoutManager = horizontalLayoutManager
//        adapterDate = RecycleViewAdapter(this, viewColors, dates)
//        recyclerView1.adapter = adapterDate


        var spinner = findViewById<View>(R.id.spinner3) as Spinner
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this@MainActivityMonthlyReport,
            android.R.layout.simple_spinner_item, paths
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)
        spinner.setOnItemSelectedListener(this)



        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
//        val navController = findNavController(R.id.navigation_home)
        navView.menu.findItem(R.id.navigation_report).isChecked = true

        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent =
                        Intent(this@MainActivityMonthlyReport, UserScheduleActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_settings -> {
                    val intent = Intent(this@MainActivityMonthlyReport, PatientSettingsActivity::class.java)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPillsDataFromDatabase(callback: (List<String>) -> Unit): MutableList<String> {
        val dbRef = FirebaseDatabase.getInstance().getReference("pills_status")
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

                Log.d("zczytanie pills", reportValuesList.toString())

                // Zwracamy wartość przez callback
                callback(reportValuesList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
        return reportValuesList
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun Create(data: List<Any>): MutableMap<LocalDate, String> {
        Log.d("cos", data.toString())

        val days = listOf(-2, -1, 0, 1, 2)
        var daysElems = mutableListOf<LocalDate>()

        val current = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = current.format(formatter)

        for(i in days){
            val date = current.plusDays(i.toLong())
            val formattedDate = date.format(formatter)
            val dateTime = LocalDate.parse(formattedDate, formatter)
            daysElems.add(dateTime)


        }



//
//
//        var day1 = "$year-$month-$day11"
//        var day2 = "$year-$month-$day22"
//        var day3 = "$year-$month-$day33"
//        var day4 = "$year-$month-$day44"
//        var day5 = "$year-$month-$day55"
//
//
//        if (day11 < 1) {
//            val prevMonth = if (month == 1) 12 else month - 1
//            val prevYear = if (month == 1) year - 1 else year
//            val lastDayOfPrevMonth = getLastDayOfMonth(prevYear, prevMonth)
//            day1 = "$prevYear-$prevMonth-$lastDayOfPrevMonth"
//        }
//        if (day22 < 1) {
//            val prevMonth = if (month == 1) 12 else month - 1
//            val prevYear = if (month == 1) year - 1 else year
//            val lastDayOfPrevMonth = getLastDayOfMonth(prevYear, prevMonth)
//            day2 = "$prevYear-$prevMonth-$lastDayOfPrevMonth"
//        }
//        if (day33 < 1) {
//            val prevMonth = if (month == 1) 12 else month - 1
//            val prevYear = if (month == 1) year - 1 else year
//            val lastDayOfPrevMonth = getLastDayOfMonth(prevYear, prevMonth)
//            day3 = "$prevYear-$prevMonth-$lastDayOfPrevMonth"
//        }
//        if (day44 < 1) {
//            val prevMonth = if (month == 1) 12 else month - 1
//            val prevYear = if (month == 1) year - 1 else year
//            val lastDayOfPrevMonth = getLastDayOfMonth(prevYear, prevMonth)
//            day4 = "$prevYear-$prevMonth-$lastDayOfPrevMonth"
//        }
//        if (day55 < 1) {
//            val prevMonth = if (month == 1) 12 else month - 1
//            val prevYear = if (month == 1) year - 1 else year
//            val lastDayOfPrevMonth = getLastDayOfMonth(prevYear, prevMonth)
//            day5 = "$prevYear-$prevMonth-$lastDayOfPrevMonth"
//        }
//        Log.d("wyglad daty", day1.toString())


        val resultDict = mutableMapOf<LocalDate, String>()
        var daysInDataBase = mutableListOf<LocalDate>()

        for (i in data.indices step 4) {
            if (data[i] is String) {
                try {
//                    var date = data[i] as String
//                    var date =  LocalDate.parse(data[i] as String, DateTimeFormatter.ISO_DATE
                    var dateTime = LocalDate.parse(data[i] as String, formatter)

//                    var value = data[i + 2] as String
                    daysInDataBase.add(dateTime)
//                    for (j in daysElems){
//                        if(date.equals(j)){
//                            resultDict[date] = value
//                        }
//                    }

                } catch (e: IndexOutOfBoundsException) {
                    continue
                }
            }
        }
        var commonElems = daysInDataBase.intersect(daysElems)
        Log.d("daysindata", daysInDataBase.toString())
        Log.d("daysElems", daysElems.toString())


        for(k in commonElems){
            resultDict[k as LocalDate] = "true"
            daysElems.removeIf{it == k}
        }
        for (l in daysElems){
            resultDict[l] = "false"
        }

        val sortedDates = resultDict.toSortedMap()

        var colors = pillsColors(sortedDates)

        val dates = arrayListOf(
            LocalDate.now().minusDays(2),
            LocalDate.now().minusDays(1),
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(2),

            )

        // set up the RecyclerView
        val recyclerView1: RecyclerView = findViewById(R.id.rvAnimals)
        val horizontalLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView1.layoutManager = horizontalLayoutManager
        adapterDate = RecycleViewAdapter(this, colors, dates)
        recyclerView1.adapter = adapterDate

        Log.d("daty pils", resultDict.toString())
        return resultDict
    }



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
                    var value = data[i + index!!] as String
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
        getPillsDataFromDatabase { data ->
            Create(data)
        }
//        getDataFromDatabase { data ->
//            val resultDict = createDict(data, selectedItem)
//            var list = getPillsDataFromDatabase { data ->
//                Create(data)
//            }
//            var colors = Colors(list)
//
//
//        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Handle no item selection here
    }
    fun getLastDayOfMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun pillsColors(list: SortedMap<LocalDate, String>): ArrayList<Int> {
        var colors = arrayListOf<Int>()
        for ((key, value) in list.entries) {
            if (value == "true") {
                colors.add(Color.GREEN)
            } else if (value == "false") {
                colors.add(Color.RED)
            }
        }
        return colors
    }



}







