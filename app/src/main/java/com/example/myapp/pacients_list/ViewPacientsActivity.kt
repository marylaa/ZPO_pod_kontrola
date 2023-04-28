package com.example.myapp.pacients_list

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.login.UserModel
import com.example.myapp.pills_list.PillItemAdapter
import com.example.myapp.pills_list.PillModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ViewPacientsActivity : AppCompatActivity() {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_doctor_page)

        newRecyclerView = findViewById(R.id.rvPacients)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)
        getDataFromDatabase()
    }

    private fun getDataFromDatabase() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        val pacientList: MutableList<UserModel> = mutableListOf()

        val doctorRef = FirebaseDatabase.getInstance().getReference("Pacients/$uid")
        doctorRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                pacientList.clear()

                val id = dataSnapshot.value
                Log.d("TAG", id.toString())

                val pacientRef = FirebaseDatabase.getInstance().getReference("Users/$id")
                pacientRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val pacient = dataSnapshot.getValue(UserModel::class.java)
                        pacientList.add(pacient!!)
                        Log.d("TAG", pacient.toString())

                        newRecyclerView.adapter = PacientItemAdapter(pacientList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("TAG", "Błąd")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
    }
}