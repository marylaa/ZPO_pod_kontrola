package com.example.myapp.monthly_report

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.EmptyActivity
import com.example.myapp.R
import com.example.myapp.databinding.ActivityMainMonthlyBinding
import com.example.myapp.pills_list.PillModel
import com.example.myapp.pills_list.UserScheduleActivity
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
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.*

class MainActivityMonthlyReport : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var adapterDate: RecycleViewAdapter
    private lateinit var dbRef: DatabaseReference
    private val paths = arrayOf(
        "Aktywność [godz.]",
        "Ciśnienie [mmHg]",
        "Cukier [mmol/L]",
        "Sen [godz.]",
        "Temp. ciała [oC]",
        "Waga [kg]"

    )
    private var pillList = ArrayList<String>()
    private var pillListId = ArrayList<String>()
    private var pillListAndCount = ArrayList<List<String>>()

    private var monthsList = arrayOf(
        "Styczeń",
        "Luty",
        "Marzec",
        "Kwiecień",
        "Maj",
        "Czerwiec",
        "Lipiec",
        "Sierpień",
        "Wrzesień",
        "Październik",
        "Listopad",
        "Grudzień"
    )
    private var dataExist: Boolean = false
    val reportValuesList = mutableListOf<String>()
    var resultDict = mutableMapOf<String, Float>()
    private lateinit var adapterPills: ArrayAdapter<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var pillId: String


    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_monthly)
        getAllPillsFromDatabase()

        var spinnerMonths = findViewById<View>(R.id.spinnerMonths) as Spinner
        val adapterMonths: ArrayAdapter<String> = ArrayAdapter<String>(
            this@MainActivityMonthlyReport,
            android.R.layout.simple_spinner_item, monthsList
        )

        adapterMonths.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonths.setAdapter(adapterMonths)

// Ustawienie bieżącego miesiąca jako wartości domyślnej
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        spinnerMonths.setSelection(currentMonth)

        spinnerMonths.setOnItemSelectedListener(this)


        var spinnerPills = findViewById<View>(R.id.spinnerPills) as Spinner

        adapterPills = ArrayAdapter(
            this@MainActivityMonthlyReport,
            android.R.layout.simple_spinner_item, pillList
        )

        adapterPills.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPills.setAdapter(adapterPills)
        if (pillList.size == 1) {
            spinnerPills.setSelection(0) // ustawienie pierwszego elementu jako aktualnie wybrany
        }
        spinnerPills.setOnItemSelectedListener(this)

        adapterPills.notifyDataSetChanged();


        var spinner = findViewById<View>(R.id.spinner3) as Spinner
        adapter = ArrayAdapter(
            this@MainActivityMonthlyReport,
            android.R.layout.simple_spinner_item, paths
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)
        spinner.setOnItemSelectedListener(this)

        adapter.notifyDataSetChanged();


        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
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
                    val intent =
                        Intent(this@MainActivityMonthlyReport, PatientSettingsActivity::class.java)
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
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                reportValuesList.clear()
                for (snapshot in dataSnapshot.children) {
                    for (rep in snapshot.children) {
                        val reportValues = rep.getValue<String>().toString()
                        reportValuesList.add(reportValues)
                    }
                }

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
        val dbRef = FirebaseDatabase.getInstance().getReference("Pills_status")
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        val query = dbRef.orderByChild("user").equalTo(uid)
        val reportValuesList = mutableListOf<String>()

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                reportValuesList.clear()
                for (snapshot in dataSnapshot.children) {
                    for (rep in snapshot.children) {
                        val reportValues = rep.getValue<String>().toString()
                        reportValuesList.add(reportValues)
                    }
                }
                callback(reportValuesList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
        return reportValuesList
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun Create(
        data: List<Any>,
        wantedMonth: String,
        wantedPill: String
    ): MutableMap<LocalDate, String> {
        val months = mapOf(
            "Styczeń" to "01",
            "Luty" to "02",
            "Marzec" to "03",
            "Kwiecień" to "04",
            "Maj" to "05",
            "Czerwiec" to "06",
            "Lipiec" to "07",
            "Sierpień" to "08",
            "Wrzesień" to "09",
            "Październik" to "10",
            "Listopad" to "11",
            "Grudzień" to "12"
        )

        val current = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val resultDict = mutableMapOf<LocalDate, String>()
        var daysInDataBase = mutableListOf<LocalDate>()
        val newDict = mutableMapOf<String, String>()
        var prevDate: LocalDate? = null
        var prevName = ""
        var count = 1

        if (pillListId.isEmpty()) {
            val intent = Intent(this, EmptyActivity::class.java)
            startActivity(intent)
        } else {
            pillId = pillListId.get(0)
        }
            if (wantedPill != "") {
                var index = pillList.indexOf(wantedPill)
                pillId = pillListId.get(index)
            }


            for (i in 0 until data.size step 4) {
                if (data[i] is String) {
                    try {
                        var pillName = data[i + 1] as String
                        var dateTime = LocalDate.parse(data[i] as String, formatter)
                        val month = dateTime.monthValue
                        val monthFormatted = String.format("%02d", month)
                        if (monthFormatted == (months[wantedMonth].toString()) && pillName == pillId) {
                            daysInDataBase.add(dateTime)
                        }

                        prevDate = dateTime
                        prevName = pillName

                    } catch (e: IndexOutOfBoundsException) {
                        continue
                    }
                }
            }

            val elementCountMap = mutableMapOf<Any, Int>()


            for (element in daysInDataBase) {
                val numberOfElems = elementCountMap[element]
                if (numberOfElems != null) {
                    elementCountMap[element] = numberOfElems + 1
                } else {
                    elementCountMap[element] = 1
                }
            }

            for ((element, numberOfElems) in elementCountMap) {
                println("Element: $element, Count: $numberOfElems")
                var freq = getPillFrequency(wantedPill)
                var number = numberOfElems.toString() + "/" + freq
                newDict[element.toString()] = number
            }


            val dictionary = mutableMapOf<String, String>()
            val year = Year.now().value // pobranie aktualnego roku
            val numberOfDaysInMonth = getNumberOfDaysInMonth(year, months[wantedMonth]!!.toInt())


            for (day in 1..numberOfDaysInMonth) {
                val dayFormatted = String.format("%02d", day)
                var date =
                    year.toString() + "-" + months[wantedMonth].toString() + "-" + dayFormatted.toString()
                dictionary[date] = "0"
            }

            for (key in dictionary.keys) {
                for ((keyNewDict, valueNewDict) in newDict.entries) {
                    if (key.toString().equals(keyNewDict)) {
                        dictionary[key] = valueNewDict
                    }
                }
            }

            val sortedDates = dictionary.toSortedMap()

            var colors = pillsColors(sortedDates)
            var dates = mutableListOf<String>()

            for (key in dictionary.keys) {
                dates.add(key)
            }

            // set up the RecyclerView
            val recyclerView1: RecyclerView = findViewById(R.id.rvAnimals)
            val horizontalLayoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            recyclerView1.layoutManager = horizontalLayoutManager
            adapterDate = RecycleViewAdapter(this, colors, dates)
            recyclerView1.adapter = adapterDate

            return resultDict

    }

    fun getPillFrequency(name: String): String? {
        for (index in pillListAndCount.indices) {
            val item = pillListAndCount[index]
            if (item.isNotEmpty() && item[0] == name) {
                return item[1]
            }
        }
        return null
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun createDict(data: List<Any>, param: String, wantedMonth: String): MutableList<Entry> {
        val dictionary = mapOf(
            "Ciśnienie [mmHg]" to 1,
            "Aktywność [godz.]" to 0,
            "Waga [kg]" to 5,
            "Sen [godz.]" to 3,
            "Temp. ciała [oC]" to 4,
            "Cukier [mmol/L]" to 2,
            "mood" to 6,
            "notes" to 7
        )
        var index = dictionary[param]

        val current = LocalDate.now()
        val formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = current.format(formatter1)

        val months = mapOf(
            "Styczeń" to "01",
            "Luty" to "02",
            "Marzec" to "03",
            "Kwiecień" to "04",
            "Maj" to "05",
            "Czerwiec" to "06",
            "Lipiec" to "07",
            "Sierpień" to "08",
            "Wrzesień" to "09",
            "Październik" to "10",
            "Listopad" to "11",
            "Grudzień" to "12"
        )

        resultDict.clear()

        for (i in data.indices step 10) {
            if (data[i] is String) {
                try {
                    var date = data[i + 6] as String
                    var value = data[i + index!!] as String
                    var dateTime = LocalDate.parse(date as String, formatter1)
                    val month = dateTime.monthValue
                    val monthFormatted = String.format("%02d", month)
                    if (monthFormatted.equals(months[wantedMonth].toString())) {
                        resultDict[date] = value.toFloat()
                    }
                } catch (e: IndexOutOfBoundsException) {
                    continue
                }
            }
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-M-d")
        var entries = resultDict.entries.map { entry ->
            var date = LocalDate.parse(entry.key, formatter)
            Entry(
                date.dayOfMonth.toFloat(),
                entry.value.toFloat()
            )
        }.sortedBy { it.x }.toMutableList()


        var lineDataSet = LineDataSet(entries, param)
        var lineData = LineData(lineDataSet)
        var lineChart = findViewById<LineChart>(R.id.lineChart)
        lineChart.description.text = "Dni miesiąca"
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

        val noDataTextView = findViewById<TextView>(R.id.noDataTextView)

        if (entries.isEmpty()) {
            lineChart.visibility = View.GONE
            noDataTextView.visibility = View.VISIBLE
        } else {
            lineChart.visibility = View.VISIBLE
            noDataTextView.visibility = View.GONE
        }
        return entries
    }

    private fun getAllPillsFromDatabase() {
        dbRef = FirebaseDatabase.getInstance().getReference("Pills")
        val user = FirebaseAuth.getInstance().currentUser;
        val uid = user?.uid

        val query = dbRef.orderByChild("pacient").equalTo(uid)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                pillList.clear()
                pillListId.clear()
                for (snapshot in dataSnapshot.children) {
                    val pill = snapshot.getValue(PillModel::class.java)
                    pillList.add(pill!!.name.toString())
                    pillListId.add(pill!!.id.toString())
                    val onePill: List<String> =
                        listOf(pill!!.name, pill!!.time_list?.size.toString())
                    pillListAndCount.add(onePill)
                    adapterPills.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        var selectedItem = ""
        selectedItem = "Ciśnienie [mmHg]"

        selectedItem = parent?.getItemAtPosition(position) as String
        Log.d("selected", selectedItem.toString())

        var selectedPill = ""

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)

        if (pillList.isNotEmpty()) {
            selectedPill = pillList[0]
        }
        var selectedMonth = findViewById<Spinner>(R.id.spinnerMonths).selectedItem.toString()

        getPillsDataFromDatabase { data ->
            Create(data, selectedMonth, selectedPill)
        }

        adapter.notifyDataSetChanged()

        when (parent?.id) {
            R.id.spinner3 -> {
                getDataFromDatabase { data ->
                    createDict(data, selectedItem, selectedMonth)
                }
            }
            R.id.spinnerPills -> {
                getPillsDataFromDatabase { data ->
                    Create(data, selectedMonth, selectedItem)
                }
                adapterPills.notifyDataSetChanged()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Handle no item selection here
    }


    fun pillsColors(list: SortedMap<String, String>): ArrayList<Int> {
        var colors = arrayListOf<Int>()
        for ((key, value) in list.entries) {
            var number = 0.0
            try {
                var splited = value.split("/")
                number = splited[0].toDouble() / splited[1].toDouble()
            } catch (e: Exception) {
                number = -1.0
            }

            if(value.equals("1/3") or value.equals("2/3") or value.equals("1/2")){
                colors.add(Color.YELLOW)
            }else if(value.equals("1/1") or value.equals("3/3") or value.equals("2/2") or (number > 1)){
                colors.add(Color.GREEN)
            }else{
                colors.add(Color.RED)
            }
        }
        return colors
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getNumberOfDaysInMonth(year: Int, month: Int): Int {
        val yearMonthObject = YearMonth.of(year, month)
        val daysInMonth = yearMonthObject.lengthOfMonth()
        return daysInMonth
    }
}