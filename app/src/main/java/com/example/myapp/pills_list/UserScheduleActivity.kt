package com.example.myapp.pills_list

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.monthly_report.MainActivityMonthlyReport
import com.example.myapp.report.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserScheduleActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference
    ;


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
        getDataFromDatabase()

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
//        val navController = findNavController(R.id.navigation_home)
        navView.menu.findItem(R.id.navigation_home).isChecked = true

        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    true
                }
                R.id.navigation_dashboard -> {
                    val intent = Intent(this@UserScheduleActivity, MainActivityMonthlyReport::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_notifications -> {
                    val intent = Intent(this@UserScheduleActivity, UserScheduleActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }



    }

//    private fun replaceFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.rvPills, fragment)
//            .commit()
//    }
//
//    class FragmentOne : Fragment() {
//
//        override fun onCreateView(
//            inflater: LayoutInflater, container: ViewGroup?,
//            savedInstanceState: Bundle?
//        ): View? {
//            // Inflate the layout for this fragment
//            return inflater.inflate(R.layout.activity_user_pills_schedule, container, false)
//        }
//
//    }
//
//    class FragmentTwo : Fragment() {
//
//        override fun onCreateView(
//            inflater: LayoutInflater, container: ViewGroup?,
//            savedInstanceState: Bundle?
//        ): View? {
//            // Inflate the layout for this fragment
//            return inflater.inflate(R.layout.activity_main_monthly, container, false)
//        }
//
//    }
//
//    private fun selectFragment(itemId: Int) {
//        var fragment: Fragment? = null
//
//        // Initialize the selected fragment based on the item id
//        when (itemId) {
//            R.id.navigation_home -> fragment = FragmentOne()
//            R.id.navigation_dashboard -> fragment = FragmentTwo()
//
//        }
//
//        // Replace the current fragment with the selected fragment
//        if (fragment != null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.rvPills, fragment)
//                .commit()
//        }
//    }


    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {

                R.id.addPill -> {
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

        private fun getDataFromDatabase() {
            dbRef = FirebaseDatabase.getInstance().getReference("Pills")
            val user = FirebaseAuth.getInstance().currentUser;
            val uid = user?.uid

            val pillList: MutableList<PillModel> = mutableListOf()

            val query = dbRef.orderByChild("pacient").equalTo(uid)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Usuń dane z listy
                    pillList.clear()
                    // Pobierz dane i dodaj do listy
                    for (snapshot in dataSnapshot.children) {
                        val pill = snapshot.getValue(PillModel::class.java)
                        pillList.add(pill!!)
                    }
                    for (item in pillList) {
                        Log.d("TAG", item.toString())
                    }
                    // utwórz adapter i przekaż listę do niego
                    newRecyclerView.adapter = PillItemAdapter(pillList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("TAG", "Błąd")
                }
            })
        }
    }


