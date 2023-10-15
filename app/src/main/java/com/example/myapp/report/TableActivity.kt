package com.example.myapp.report

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.R

class TableActivity: AppCompatActivity() {

    lateinit var languageLV: ListView
    lateinit var lngList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.monthly_report_table)

        val value = intent.getStringExtra("key")
        var dataList = value?.toList()

        // on below line we are initializing our variables.
        languageLV = findViewById(R.id.idLVLanguages)

        val adapter: ArrayAdapter<String?> = ArrayAdapter<String?>(
            this@TableActivity,
            android.R.layout.simple_list_item_1,
            dataList as List<String?>
        )

        // on below line we are setting adapter for our list view.
        languageLV.adapter = adapter
    }
}
