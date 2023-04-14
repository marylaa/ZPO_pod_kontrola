package com.example.myapp.pills_list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R

class UserScheduleActivity : AppCompatActivity() {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArrayList: ArrayList<PillItem>
    lateinit var hour: Array<String>
    lateinit var name: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_pills_schedule)

        name = arrayOf(
            "Lek1",
            "Lek2",
            "Lek3",
            "Lek4",
            "Lek5",
            "Lek6",
            "Lek7",
            "Lek8")

        hour = arrayOf(
            "15:00",
            "16:00",
            "17:00",
            "18:00",
            "19:00",
            "20:00",
            "21:00",
            "22:00")

//        val uID = intent
//        val userID = uID.getStringExtra("uID")

        newRecyclerView = findViewById(R.id.rvPills)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)
        newArrayList = arrayListOf<PillItem>()
        getUserData()
    }

    private fun getUserData() {
        for(i in name.indices) {
            val pill = PillItem(name[i], hour[i])
            newArrayList.add(pill)
        }
        newRecyclerView.adapter = PillItemAdapter(newArrayList)
    }
}


