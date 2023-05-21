//package com.example.myapp.report
//
//import android.app.ActionBar
//import android.content.Context
//import android.os.Bundle
//import android.util.Log
//import android.widget.TableLayout
//import android.widget.TableRow
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import com.example.myapp.R
//
//
//class PillsTable : AppCompatActivity() {
//
//    private val dataToTable = mutableListOf<List<String>>()
//    private var dataList = mutableListOf<List<String>>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.monthly_report_table)
//
//        Log.d("przed tabela", dataList.toString())
//
//        Log.d("tabela", dataList.toString())
//        val tableLayout = findViewById<TableLayout>(R.id.tableLayout)
//
//
//    }
//
//    fun setData(dataList: MutableList<List<String>>) {
//        this.dataList = dataList
//        Log.d("data w table", dataList.toString())
//
//        setContentView(R.layout.monthly_report_table)
//        val tableLayout = findViewById<TableLayout>(R.id.tableLayout)
//        tableLayout.removeAllViews() // Usuń istniejące wiersze tabeli
//
//        for (data in dataList) {
//            val tableRow = TableRow(this)
//            val layoutParams = ActionBar.LayoutParams(
//                ActionBar.LayoutParams.MATCH_PARENT,
//                ActionBar.LayoutParams.WRAP_CONTENT
//            )
//            tableRow.layoutParams = layoutParams
//
//            val textView = TextView(this)
//            textView.text = data.toString()
//            tableRow.addView(textView)
//
//            tableLayout.addView(tableRow)
//        }
//    }
//
//
//}
//
