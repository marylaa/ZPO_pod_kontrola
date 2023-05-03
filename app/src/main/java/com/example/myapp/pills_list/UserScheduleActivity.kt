package com.example.myapp.pills_list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.monthly_report.MainActivityMonthlyReport
import com.example.myapp.report.MainActivity
import com.example.myapp.settings.PatientSettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import com.example.myapp.pills_list.TestAdapter.Callback


class UserScheduleActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_pills_schedule)

        val addButton = findViewById<Button>(R.id.addPill)
        addButton.setOnClickListener(this)

        val addReport = findViewById<Button>(R.id.addRaport)
        addReport.setOnClickListener(this)



        newRecyclerView = findViewById(R.id.rvPills)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)
        var pillsList = getDataFromDatabase()
//        saveCheckedState(pillsList)
//
//        val adapter = TestAdapter()
//        adapter.setCallback(object : Callback {
//            override fun onCheckedChanged(item: String?, isChecked: Boolean) {
//                Log.d("checked", item.toString())
//                Log.d("cheked1", isChecked.toString())
//            }
//        })
//
//        adapter.addItem(pillsList)


        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
//        val navController = findNavController(R.id.navigation_home)
        navView.menu.findItem(R.id.navigation_home).isChecked = true

        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    true
                }
                R.id.navigation_report -> {
                    val intent = Intent(this@UserScheduleActivity, MainActivityMonthlyReport::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_settings -> {
                    val intent = Intent(this@UserScheduleActivity, PatientSettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }



    }

    override fun onClick(view: View?) {
        if(view !=null){
            when (view.id){

                R.id.addPill ->{
                    val intent = Intent(this, AddPillActivity::class.java)
                    startActivity(intent)
                };
                R.id.addRaport -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

//    fun saveCheckedState(list: MutableList<PillModel>) {
//        val checkedItems = mutableListOf<Int>()
//        val user = FirebaseAuth.getInstance().currentUser;
//        val uid = user?.uid
//        val c = Calendar.getInstance()
//        val year = c.get(Calendar.YEAR)
//        val month = c.get(Calendar.MONTH) + 1
//        val day = c.get(Calendar.DAY_OF_MONTH)
//
//        var date = "$year-$month-$day"
//
//        for (i in list.indices) {
//            checkedItems.add(i)
//        }
//    }

    private fun getDataFromDatabase(): MutableList<PillModel> {
        dbRef = FirebaseDatabase.getInstance().getReference("Pills")
        val user = FirebaseAuth.getInstance().currentUser;
        val uid = user?.uid

        val pillList: MutableList<PillModel> = mutableListOf()

        val query = dbRef.orderByChild("pacient").equalTo(uid)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                pillList.clear()
                for (snapshot in dataSnapshot.children) {
                    val pill = snapshot.getValue(PillModel::class.java)
                    pillList.add(pill!!)
                }
                newRecyclerView.adapter = PillItemAdapter(pillList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
        return pillList
    }
}


