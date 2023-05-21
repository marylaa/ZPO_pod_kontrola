package com.example.myapp.doctor_view

import com.example.myapp.monthly_report.RecycleViewAdapter
import com.example.myapp.monthly_report.RecycleViewAdapterItem



//import kotlinx.android.synthetic.main.contact_monthly.*
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.EmptyActivityDoctor
import com.example.myapp.R
import com.example.myapp.databinding.ActivityMainMonthlyBinding
import com.example.myapp.pills_list.PillModel
import com.example.myapp.report.TableActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.*


class MonthyReportDoctor : AppCompatActivity(), AdapterView.OnItemSelectedListener, View.OnClickListener {

    private lateinit var adapterDate: RecycleViewAdapter
    private lateinit var adapter: RecycleViewAdapterItem
    lateinit var linelist: ArrayList<Entry>
    lateinit var lineDataSet: LineDataSet
    lateinit var lineData: LineData
    private lateinit var binding: ActivityMainMonthlyBinding
    private lateinit var dbRef: DatabaseReference
    private val paths = arrayOf("Ciśnienie [mmHg]", "Aktywność [godz.]", "Waga [kg]","Sen [godz.]","Cukier [mmol/L]","Temp. ciała [oC]"  )
    private var pillList = ArrayList<String>()

    //    private var pillList = arrayOf()
    private var monthsList = arrayOf("Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec", "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień")

    private var patientId: String? = null
    private lateinit var adapterPills: ArrayAdapter<String>
    private val dataToTable = mutableListOf<List<String>>()




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_monthly_doctor)

        patientId = intent.getStringExtra("patientId")
        Log.d("id", patientId.toString())

        getAllPillsFromDatabase()


        val button = findViewById<Button>(R.id.pills)
        button.setOnClickListener(this)




        var spinnerMonths = findViewById<View>(R.id.spinnerMonths) as Spinner
        val adapterMonths: ArrayAdapter<String> = ArrayAdapter<String>(
            this@MonthyReportDoctor,
            android.R.layout.simple_spinner_item, monthsList
        )

        adapterMonths.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonths.setAdapter(adapterMonths)
        spinnerMonths.setOnItemSelectedListener(this)


        var spinnerPills = findViewById<View>(R.id.spinnerPills) as Spinner
        adapterPills = ArrayAdapter(
            this@MonthyReportDoctor,
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
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this@MonthyReportDoctor,
            android.R.layout.simple_spinner_item, paths
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)
        spinner.setOnItemSelectedListener(this)



//        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
////        val navController = findNavController(R.id.navigation_home)
//        navView.menu.findItem(R.id.navigation_report).isChecked = true
//
//        navView.setOnNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.navigation_home -> {
//                    val intent =
//                        Intent(this@MainActivityMonthlyReport, UserScheduleActivity::class.java)
//                    startActivity(intent)
//                    true
//                }
//                R.id.navigation_settings -> {
//                    val intent = Intent(this@MainActivityMonthlyReport, PatientSettingsActivity::class.java)
//                    startActivity(intent)
//                    true
//                }
//                else -> false
//            }
//        }

    }



    private fun getDataFromDatabase(callback: (List<String>) -> Unit): MutableList<String> {
        val dbRef = FirebaseDatabase.getInstance().getReference("report")
        val uid = patientId



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
        val uid = patientId

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
    private fun Create(data: List<Any>, wantedMonth: String, wantedPill: String): MutableMap<LocalDate, String> {
        Log.d("cos", data.toString())



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
        val formatted = current.format(formatter)





        val resultDict = mutableMapOf<LocalDate, String>()
        var daysInDataBase = mutableListOf<LocalDate>()

        for (i in data.indices step 4) {
            for (j in 1 until data.size step 4) {
                if (data[i] is String) {
                    try {
                        var pillName = data[j] as String
                        var dateTime = LocalDate.parse(data[i] as String, formatter)
                        val month = dateTime.monthValue
                        val monthFormatted = String.format("%02d", month)
                        if (monthFormatted.equals(months[wantedMonth].toString()) && pillName.equals(wantedPill)) {
                            daysInDataBase.add(dateTime)
                        }


                    } catch (e: IndexOutOfBoundsException) {
                        continue
                    }
                }
            }
        }

        Log.d("days", daysInDataBase.toString())



        val dictionary = mutableMapOf<String, Boolean>()

//        // Wszystkie dni miesiąca są inicjalizowane na false
        val year = Year.now().value // pobranie aktualnego roku


        val numberOfDaysInMonth = getNumberOfDaysInMonth(year, months[wantedMonth]!!.toInt() )


        for (day in 1..numberOfDaysInMonth) {
//            val date = LocalDate.of(year, months[wantedMonth]!!.toInt() + 1, day)
            val dayFormatted = String.format("%02d", day)
            var date = year.toString() + "-" + months[wantedMonth].toString() + "-" + dayFormatted.toString()
            dictionary[date] = false
        }

        Log.d("slownik daty", dictionary.toString())

        for (key in dictionary.keys) {
            for(i in daysInDataBase){
                if(key.toString().equals(i.toString())){
                    dictionary[key] = true
                }
            }
        }

        Log.d("slownik daty zmiana", dictionary.toString())




        val sortedDates = dictionary.toSortedMap()

        var colors = pillsColors(sortedDates)
        Log.d("colors", colors.toString())

        var dates = mutableListOf<String>()

        for (key in dictionary.keys) {
            dates.add(key)
        }

        // set up the RecyclerView
//        val recyclerView1: RecyclerView = findViewById(R.id.rvAnimals)
//        val horizontalLayoutManager =
//            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        recyclerView1.layoutManager = horizontalLayoutManager
//        adapterDate = RecycleViewAdapter(this, colors,dates )
//        recyclerView1.adapter = adapterDate

        Log.d("daty pils", resultDict.toString())
        return resultDict
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun createDict(data: List<Any>, param: String, wantedMonth: String): MutableList<Entry> {
        val dictionary = mapOf("Ciśnienie [mmHg]" to 0, "Aktywność [godz.]" to 1, "Waga [kg]" to 2, "Sen [godz.]" to 3, "Temp. ciała [oC]" to 4, "Cukier [mmol/L]" to 5, "mood" to 6, "notes" to 7)
        var index = dictionary[param]
        var resultDict = mutableMapOf<String, Float>()

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

//        val moodDict = mutableMapOf<String, String>()
//        val notesDict = mutableMapOf<String, String>()
//        val pressureDict = mutableMapOf<String, String>()
        for (i in data.indices step 10) {
            if (data[i] is String) {
                try {
                    var date = data[i+6] as String
                    var value = data[i + index!!] as String
                    var dateTime = LocalDate.parse(date as String, formatter1)
                    val month = dateTime.monthValue
                    val monthFormatted = String.format("%02d", month)
                    if (monthFormatted.equals(months[wantedMonth].toString())) {
                        resultDict[date] = value.toFloat()

                    }
//                    resultDict[date] = value.toFloat()

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

    private fun getAllPillsFromDatabase(){


        dbRef = FirebaseDatabase.getInstance().getReference("Pills")
        val uid = patientId


//        val pillList: MutableList<String> = mutableListOf()

        val query = dbRef.orderByChild("pacient").equalTo(uid)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                pillList.clear()
                for (snapshot in dataSnapshot.children) {
                    val pill = snapshot.getValue(PillModel::class.java)
//                    pillList.add(pill!!.getName())
                    pillList.add(pill!!.name.toString())
                    Log.d("pils", pillList.toString())
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
    }



//
//    private fun getDataFromDatabase11() {
//        val dbRef = FirebaseDatabase.getInstance().getReference("report")
//        val user = FirebaseAuth.getInstance().currentUser
//        val uid = user?.uid
//
//        val query = dbRef.orderByChild("user").equalTo(uid)
//
//        query.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val reports = mutableListOf<Report>()
//
//                for (snapshot in dataSnapshot.children) {
//                    val report = snapshot.getValue(Report::class.java)
//                    report?.let { reports.add(it) }
//                }
//
//                val valuesList = reports.firstOrNull()?.valuesList?.map { it.toString() }?.joinToString(", ")
//                val date = reports.firstOrNull()?.date ?: ""
//                val mood = reports.firstOrNull()?.mood ?: ""
//                val notes = reports.firstOrNull()?.notes ?: ""
//                val user = reports.firstOrNull()?.user ?: ""
//
//                val result = "Value: $valuesList, date: $date, mood: $mood, notes: $notes, user: $user"
//                Log.d("TAG", result)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("TAG", "Błąd")
//            }
//        })
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = parent?.getItemAtPosition(position) as String
        Log.d("selected", selectedItem.toString())

        getAllPillsFromDatabase()

        Log.d("date", pillList.toString())
        var selectedPill = ""

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)


        if (pillList.size == 0) {
            val intent = Intent(this@MonthyReportDoctor, EmptyActivityDoctor::class.java)
            startActivity(intent)
        }

        if(pillList.size == 1){
            selectedPill = pillList[0]

        }


        val selectedMonth = findViewById<Spinner>(R.id.spinnerMonths).selectedItem.toString()


        getPillsDataFromDatabase { data ->
            Create(data, selectedMonth, selectedPill)
            Log.d("callback", data.toString())

             var index = 0

                while (index < data.size) {
                    val rowData = mutableListOf<String>()

                    val date = data[index] as String
                    val pill = data[index+1] as String
                    val status = data[index+2] as String

                    rowData.add(date)
                    rowData.add(pill)
                    rowData.add(status)

                    dataToTable.add(rowData)
                    index+=4


                }

//            val pillsTable = PillsTable()
//            pillsTable.setData(dataToTable)
//            TableActivity(dataToTable)
        }
        adapterPills.notifyDataSetChanged()


        when (parent?.id) {
            R.id.spinner3 -> {
                getDataFromDatabase { data ->
                    createDict(data, selectedItem, selectedMonth)
                }
            }
            R.id.spinnerPills -> {
                getPillsDataFromDatabase { data ->
                    Create(data, selectedMonth, selectedItem)
                    adapterPills.notifyDataSetChanged()
//                    Log.d("callback", data.toString())
//                    val pillsTable = PillsTable()
//                    pillsTable.setData(data)
                }
                adapterPills.notifyDataSetChanged()

            }

        }

    }



    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Handle no item selection here
    }


    fun pillsColors(list: SortedMap<String, Boolean>): ArrayList<Int> {
        var colors = arrayListOf<Int>()
        for ((key, value) in list.entries) {
            if (value.equals(true)) {
                colors.add(Color.GREEN)
            } else if (value.equals(false)) {
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

    fun setPacient(pacientId: String){
        this.patientId = patientId
    }

    override fun onClick(view: View?) {
        if(view !=null){
            when (view.id){
                R.id.pills ->{

//                    TableActivity(dataToTable)
                    val intent = Intent(this, TableActivity::class.java)
//                    intent.putExtra("key", dataToTable.joinToString(", ")) // Przekazanie wartości jako dodatkowy parametr
                    val arrayList: ArrayList<String> = ArrayList(dataToTable.flatten())
                    intent.putStringArrayListExtra("dataList", arrayList)

                    startActivity(intent)
                }
            }
        }
    }
}













