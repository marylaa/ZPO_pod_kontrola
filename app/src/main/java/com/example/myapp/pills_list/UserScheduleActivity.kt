package com.example.myapp.pills_list

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.AddPillActivity
import com.example.myapp.R

class UserScheduleActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArrayList: ArrayList<PillItem>
    lateinit var hour: Array<String>
    lateinit var name: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_pills_schedule)

        val addButton = findViewById<Button>(R.id.addPill)
        addButton.setOnClickListener(this)

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

        newRecyclerView = findViewById(R.id.rvPills)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)
        newArrayList = arrayListOf<PillItem>()
        getUserData()
    }

    override fun onClick(view: View?) {
        if(view !=null){
            when (view.id){

                R.id.addPill ->{
                    val intent = Intent(this, AddPillActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun getUserData() {
        for(i in name.indices) {
            val pill = PillItem(name[i], hour[i])
            newArrayList.add(pill)
        }
        newRecyclerView.adapter = PillItemAdapter(newArrayList)
    }
}


