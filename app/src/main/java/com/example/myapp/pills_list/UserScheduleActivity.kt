package com.example.myapp.pills_list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.settings.PacientSettingsActivity
import com.example.myapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserScheduleActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_pills_schedule)

        val addButton = findViewById<Button>(R.id.addPill)
        addButton.setOnClickListener(this)

        newRecyclerView = findViewById(R.id.rvPills)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)
        getDataFromDatabase()

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.menu.findItem(R.id.navigation_home).isChecked = true

        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@UserScheduleActivity, UserScheduleActivity::class.java)
                    startActivity(intent)
                    true
                }
//                R.id.navigation_report -> {
//                    val intent = Intent(this@AddPillActivity, MainActivityMonthlyReport::class.java)
//                    startActivity(intent)
//                    true
//                }
                R.id.navigation_settings -> {
                    val intent = Intent(this@UserScheduleActivity, PacientSettingsActivity::class.java)
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
                }
            }
        }
    }

    private fun getDataFromDatabase() {
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
//                for (item in pillList) {
//                    Log.d("TAG", item.toString())
//                }
                newRecyclerView.adapter = PillItemAdapter(pillList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
    }
}


