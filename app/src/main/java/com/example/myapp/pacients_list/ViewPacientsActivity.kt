//package com.example.myapp.pacients_list
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.Button
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.myapp.R
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.*
//
//class ViewPacientsActivity : AppCompatActivity(), View.OnClickListener {
//
//    private lateinit var newRecyclerView: RecyclerView
//    private lateinit var dbRef: DatabaseReference
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main_doctor_page)
//
//        newRecyclerView = findViewById(R.id.rvPacients)
//        newRecyclerView.layoutManager = LinearLayoutManager(this)
//        newRecyclerView.setHasFixedSize(true)
////        getDataFromDatabase()
//    }
//
////    private fun getDataFromDatabase() {
////        dbRef = FirebaseDatabase.getInstance().getReference("Pills")
////        val user = FirebaseAuth.getInstance().currentUser;
////        val uid = user?.uid
////
////        val pillList: MutableList<PacientModel> = mutableListOf()
////
////        val query = dbRef.orderByChild("pacient").equalTo(uid)
////        query.addValueEventListener(object : ValueEventListener {
////            override fun onDataChange(dataSnapshot: DataSnapshot) {
////                // Usuń dane z listy
////                pillList.clear()
////                // Pobierz dane i dodaj do listy
////                for (snapshot in dataSnapshot.children) {
////                    val pill = snapshot.getValue(PacientModel::class.java)
////                    pillList.add(pill!!)
////                }
////                for (item in pillList) {
////                    Log.d("TAG", item.toString())
////                }
////                // utwórz adapter i przekaż listę do niego
////                newRecyclerView.adapter = PacientItemAdapter(pillList)
////            }
////
////            override fun onCancelled(error: DatabaseError) {
////                Log.d("TAG", "Błąd")
////            }
////        })
////    }
//}
//
//
