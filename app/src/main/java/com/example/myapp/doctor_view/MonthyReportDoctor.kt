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
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.EmptyActivity
import com.example.myapp.EmptyActivityDoctor
import com.example.myapp.R
import com.example.myapp.SharedObject
import com.example.myapp.databinding.ActivityMainMonthlyBinding
import com.example.myapp.monthly_report.MainActivityMonthlyReport
import com.example.myapp.patients_list.ViewPatientsActivity
import com.example.myapp.pills_list.PillModel
import com.example.myapp.pills_list.PillModelCustom
import com.example.myapp.report.TableActivity
import com.example.myapp.settings.DoctorSettingsActivity
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
import java.text.SimpleDateFormat
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
    private var pillListId = ArrayList<String>()
    private var sortedDates = mutableMapOf<String, String>()
    private var pillListAndCount = ArrayList<MainActivityMonthlyReport.Obj>()
    private var pillListAndCountCustom = ArrayList<MainActivityMonthlyReport.ObjCustom>()
    private var pillListFreq = mutableMapOf<String, String>()



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

        val backButton = findViewById<ImageButton>(R.id.back)
        backButton.setOnClickListener(this)




        var spinnerMonths = findViewById<View>(R.id.spinnerMonths) as Spinner
        val adapterMonths: ArrayAdapter<String> = ArrayAdapter<String>(
            this@MonthyReportDoctor,
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



        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@MonthyReportDoctor, ViewPatientsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_settings -> {
                    val intent = Intent(this@MonthyReportDoctor, DoctorSettingsActivity::class.java)
                    intent.putExtra("patientIds", SharedObject.getlistPacientIds())
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
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
        val dbRef = FirebaseDatabase.getInstance().getReference("Pills_status")
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
        SharedObject.setWantedPill(wantedPill)



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
        val newDict = mutableMapOf<String, String>()
        var prevDate: LocalDate? = null
        var prevName = ""
        var count = 1

        var pillId = pillListId.get(0)
        if (wantedPill != "") {
            var index = pillList.indexOf(wantedPill)
            pillId = pillListId.get(index)
        }

        for (i in 0 until data.size step 5) {
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

        Log.d("new dict", newDict.toString())

        Log.d("days", daysInDataBase.toString())



        val dictionary = mutableMapOf<String, String>()

//        // Wszystkie dni miesiąca są inicjalizowane na false
        val year = Year.now().value // pobranie aktualnego roku


        val numberOfDaysInMonth = getNumberOfDaysInMonth(year, months[wantedMonth]!!.toInt() )


        for (day in 1..numberOfDaysInMonth) {
//            val date = LocalDate.of(year, months[wantedMonth]!!.toInt() + 1, day)
            val dayFormatted = String.format("%02d", day)
            var date = year.toString() + "-" + months[wantedMonth].toString() + "-" + dayFormatted.toString()
            dictionary[date] = "0"
        }

        Log.d("slownik daty", dictionary.toString())

        for (key in dictionary.keys) {
            for((keyNewDict,valueNewDict) in newDict.entries){
                if(key.toString().equals(keyNewDict)){
                    dictionary[key] = valueNewDict
                }
            }
        }

        Log.d("slownik daty zmiana", dictionary.toString())


        sortedDates = dictionary.toSortedMap()

        SharedObject.setSortedDates(sortedDates)

        var colors = pillsColors(sortedDates as SortedMap<String, String>)
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

    fun getPillFrequency(name: String): String? {
        for (obj in pillListAndCount) {
            if (obj.name == name) {
                return obj.lenght
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

        resultDict.clear()

        for (i in data.indices step 10) {
            if (data[i] is String) {
                try {
                    var date = data[i+6] as String
                    var value = data[i + index!!] as String
                    var dateTime = LocalDate.parse(date as String, formatter1)
                    val month = dateTime.monthValue
                    val monthFormatted = String.format("%02d", month)
                    if (monthFormatted.equals(months[wantedMonth].toString())) {
                        resultDict[date] = value.replace(",", ".").toFloat()

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


        Log.d("dic", resultDict.keys.toString())

        val noDataTextView = findViewById<TextView>(R.id.noDataTextView)

        if (entries.isEmpty()) {
            lineChart.visibility = View.GONE
            noDataTextView.visibility = View.VISIBLE
        } else {
            lineChart.visibility = View.VISIBLE
            noDataTextView.visibility = View.GONE

            // Reszta kodu wykresu
        }


        return entries


    }

    private fun getAllPillsFromDatabase() {
        dbRef = FirebaseDatabase.getInstance().getReference("Pills")
        val uid = patientId


        val query = dbRef.orderByChild("pacient").equalTo(uid)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                pillList.clear()
                pillListId.clear()
                pillListFreq.clear()
                for (snapshot in dataSnapshot.children) {
                    val frequencyValue = snapshot.child("frequency").getValue(String::class.java)
                    var pill: PillModel? = null
                    var pillCustom: PillModelCustom? = null

                    if (frequencyValue != null && frequencyValue != "Niestandardowa") {
                        pill = snapshot.getValue(PillModel::class.java)
                        pillList.add(pill!!.name.toString())
                        pillListId.add(pill!!.id.toString())
                        pillListFreq.put(pill!!.name.toString(), pill!!.frequency.toString())
                        val onePill = MainActivityMonthlyReport.Obj(
                            name = pill!!.name.toString(),
                            lenght = pill!!.time_list?.size.toString(),
                            timeList = pill!!.time_list
                        )
                        pillListAndCount.add(onePill)
                        adapterPills.notifyDataSetChanged()
                    } else {
                        pillCustom = snapshot.getValue(PillModelCustom::class.java)
                        pillList.add(pillCustom!!.name.toString())
                        pillListId.add(pillCustom!!.id.toString())
                        pillListFreq.put(
                            pillCustom!!.name.toString(),
                            pillCustom!!.frequency.toString()
                        )
                        val onePill = MainActivityMonthlyReport.ObjCustom(
                            name = pillCustom!!.name.toString(),
                            lenght = pillCustom!!.time_list?.size.toString(),
                            timeList = pillCustom!!.time_list
                        )
                        pillListAndCountCustom.add(onePill)
                        adapterPills.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun CreateCustom(
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

        SharedObject.setWantedPill(wantedPill)


        print("days in database" + daysInDataBase.toString())


        var pillId = pillListId.get(0)
        if (wantedPill != "") {
            var index = pillList.indexOf(wantedPill)
            pillId = pillListId.get(index)
        }


        println(data.toString())

        for (i in 0 until data.size step 5) {
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

        println("days in database")
        print("days in database" + daysInDataBase.toString())




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
            // Znajdź dzień tygodnia dla daty
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val localDate = LocalDate.parse(element.toString(), formatter)
            val dayOfWeekStr = localDate.dayOfWeek.toString()


            var freq = getPillFrequencyCustom(wantedPill, dayOfWeekStr)
            Log.d("aa",freq.toString())
            var number = numberOfElems.toString() + "/" + freq.toString()
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

        SharedObject.setSortedDates(sortedDates)

        var colors = pillsColorsCustom(sortedDates, wantedPill)
        var dates = mutableListOf<String>()

        for (key in dictionary.keys) {
            dates.add(key)
        }

        return resultDict

    }

    data class ObjCustom(val name: String, val lenght: String, val timeList: MutableList<HashMap<String, Any>>?)
    data class Obj(val name: String, val lenght: String, val timeList: MutableList<MutableList<Any?>?>?)


    fun getPillFrequencyCustom(name: String, dayOfWeek: String): Int? {
        for (pill in pillListAndCountCustom) {
            if (pill.name.equals(name)) {

                val rawTimesList = pill.timeList

                println(rawTimesList)


                val translatedDayOfWeek = translateToPolish(dayOfWeek)

                val dayElement = rawTimesList!!.find { it["day"] == translatedDayOfWeek }
                val timesCount = dayElement?.get("times") as? List<List<Any>> ?: emptyList()

                println("Times count for $dayOfWeek: ${timesCount.size}")

                return timesCount.size

            }
        }
        return null
    }
    fun translateToPolish(dayOfWeek: String): String {
        // Dodaj odpowiednie tłumaczenia dni tygodnia, jeśli są inne niż angielskie
        val translations = mapOf(
            "MONDAY" to "Poniedziałek",
            "TUESDAY" to "Wtorek",
            "WEDNESDAY" to "Środa",
            "THURSDAY" to "Czwartek",
            "FRIDAY" to "Piątek",
            "SATURDAY" to "Sobota",
            "SUNDAY" to "Niedziela"
        )

        return translations[dayOfWeek] ?: dayOfWeek
    }


//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        val selectedItem = parent?.getItemAtPosition(position) as String
//        Log.d("selected", selectedItem.toString())
//
//        var selectedPill = ""
//
//        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
//
////        if (pillList.size == 0) {
////                navView.menu.findItem(R.id.navigation_report).isChecked = false
////                val intent = Intent(this@MainActivityMonthlyReport, EmptyActivity::class.java)
////                startActivity(intent)
////            }
//
//        if (pillList.isNotEmpty()) {
//            selectedPill = pillList[0]
//        }
//
//
//        val selectedMonth = findViewById<Spinner>(R.id.spinnerMonths).selectedItem.toString()
//
//
//        getPillsDataFromDatabase { data ->
//            Create(data, selectedMonth, selectedPill)
//
//        }
//
//
//
//
//
//
//
//
//        when (parent?.id) {
//            R.id.spinner3 -> {
//                getDataFromDatabase { data ->
//                    createDict(data, selectedItem, selectedMonth)
//                }
//            }
//            R.id.spinnerPills -> {
//                getPillsDataFromDatabase { data ->
//                    Create(data, selectedMonth, selectedItem)
//
//                }
//                adapterPills.notifyDataSetChanged()
//            }
//
//        }
//
//
//    }


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


        println(pillListFreq)

        if (!pillListFreq[selectedItem].equals("Niestandardowa", ignoreCase = true)) {
            getPillsDataFromDatabase { data ->
                Create(data, selectedMonth, selectedPill)

            }

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
        } else {
            println("niestandardowe")

            getPillsDataFromDatabase { data ->
                CreateCustom(data, selectedMonth, selectedPill)
            }


            when (parent?.id) {
                R.id.spinner3 -> {
                    getDataFromDatabase { data ->
                        createDict(data, selectedItem, selectedMonth)
                    }
                }
                R.id.spinnerPills -> {
                    getPillsDataFromDatabase { data ->
                        CreateCustom(data, selectedMonth, selectedItem)
                    }
                    adapterPills.notifyDataSetChanged()
                }
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
                Log.e("value", value)
                Log.e("splited", splited.toString())
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

    fun pillsColorsCustom(list: SortedMap<String, String>, wantedPill: String): ArrayList<Int> {

        var days = arrayListOf<String>()

        for (pill in pillListAndCountCustom) {
            if (pill.name.equals(wantedPill)) {

                val rawTimesList = pill.timeList
                for (x in rawTimesList!!) {

                    days.add(x?.get("day").toString())

                }


            }
        }





        var colors = arrayListOf<Int>()
        for ((key, value) in list.entries) {
            println(value)
            var number = 0.0
            try {
                var splited = value.split("/")
                number = splited[0].toDouble() / splited[1].toDouble()
            } catch (e: Exception) {
                number = -1.0
            }
            // Dodaj logikę sprawdzającą dzień tygodnia
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormatter.parse(key)

            val calendar = Calendar.getInstance()
            calendar.time = date

            val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale("pl", "PL"))

            println(dayOfWeek)

            if (days.any { it.equals(dayOfWeek, ignoreCase = true) }) {
                if (value.equals("1/3") or value.equals("2/3") or value.equals("1/2")) {
                    colors.add(Color.YELLOW)
                } else if (value.equals("1/1") or value.equals("3/3") or value.equals("2/2") or (number > 1)) {
                    colors.add(Color.GREEN)
                }else {
                    colors.add(Color.RED)
                }

            }else{
                colors.add(Color.GRAY)
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
//                    val intent = Intent(this, TableActivity::class.java)
////                    intent.putExtra("key", dataToTable.joinToString(", ")) // Przekazanie wartości jako dodatkowy parametr
//                    val arrayList: ArrayList<String> = ArrayList(dataToTable.flatten())
//                    intent.putStringArrayListExtra("dataList", arrayList)
                    patientId?.let { SharedObject.setPacientId(it) }
                    val intent = Intent(this,PillsStatusMain::class.java)

                    startActivity(intent)
                }
                R.id.back -> {
                    val intent = Intent(this, PatientActionsActivity::class.java)
                    intent.putExtra("patientId", SharedObject.getPacientId())
                    startActivity(intent)
                }
            }
        }
    }
}













