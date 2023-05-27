package com.example.myapp.pills_list

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.report.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


//class PillItemAdapter(private val pillList: MutableList<PillModel>?): RecyclerView.Adapter<PillItemAdapter.PillItemViewHolder>() {
class PillItemAdapter(private val pillList: MutableList<PillModel>?, private val context: Context,) : RecyclerView.Adapter<PillItemAdapter.PillItemViewHolder>() {



    private var isUpdateExecuted = false // Dodana zmienna
//    private var oldAvailability: Int = 0
    private var pillId : String = ""
    private var counter : Int = 1
    private var pillName : String = ""
    private lateinit var dbRef: DatabaseReference
    private var availabilityPill: String = ""
    private var basicAvailability: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_pill, parent, false)
        return PillItemViewHolder(itemView)
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: PillItemViewHolder, position: Int) {
        val currentItem = pillList!![position]
//        oldAvailability = currentItem.availability!!
//        pillId = currentItem.id!!
        counter = 1




        var timesADay = currentItem.time_list!!
        holder.time1.text = timesADay[0][0].toString()
        if (timesADay.size === 2) {
            holder.checkBox2.setVisibility(View.VISIBLE);
            holder.time2.setVisibility(View.VISIBLE);
            holder.time2.text = timesADay[1][0].toString()
        } else if (timesADay.size === 3) {
            holder.checkBox2.setVisibility(View.VISIBLE);
            holder.time2.setVisibility(View.VISIBLE);
            holder.time2.text = timesADay[1][0].toString()
            holder.checkBox3.setVisibility(View.VISIBLE);
            holder.time3.setVisibility(View.VISIBLE);
            holder.time3.text = timesADay[2][0].toString()
        }

//        val zoneId = ZoneId.of("Europe/Warsaw")
//        val currentTime = LocalTime.now(zoneId)  //wyswietlenie najblizszej godziny przyjecia tabletki
//        for (i in 0 until timesADay.size) {
//            val timeInList = timesADay[i][0].toString().split(":")
//            val hour = LocalTime.of(timeInList[0].toInt(), timeInList[1].toInt())
//            if(!hour.isBefore(currentTime)) {
//                holder.time.text = timesADay[i][0].toString()
//                break
//            } else if (i == timesADay.size - 1) {
//                holder.time.text = timesADay[i][0].toString()
//            }
//        }

        holder.pillTitle.text = currentItem.name
        val dbFirebase = FirebaseDatabase.getInstance()
        val dbReference = dbFirebase.getReference()

        val user = FirebaseAuth.getInstance().currentUser;
        val uid = user?.uid


        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val current = LocalDate.now().format(formatter)



        holder.itemView.apply {
            holder.checkBox1.isChecked = timesADay[0][1] as Boolean
            holder.checkBox1.setOnCheckedChangeListener { _, isChecked ->
                currentItem.time_list!![0][1] = isChecked
                Log.d("tutaj", "tutaj")

                    if (isChecked) {
                        val database = FirebaseDatabase.getInstance().getReference("Pills")
                        database.child(currentItem.id!!).setValue(currentItem)

                        // Wyświetlenie powiadomienia o wzięciu tabletki
                        val context = holder.itemView.context
                        Toast.makeText(context, "Tabletka została wzięta", Toast.LENGTH_SHORT)
                            .show()

                        var newAvailability = 0
                        var oldAvailability = 0
                        Log.d("old av", oldAvailability.toString())

                        pillId = currentItem.id!!


                        dbRef = FirebaseDatabase.getInstance().getReference("Pills")
                        val userId = FirebaseAuth.getInstance().currentUser!!.uid


                        val query = dbRef.orderByChild("pacient").equalTo(userId)
                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (snapshot in dataSnapshot.children) {
                                    val pillModel = snapshot.getValue(PillModel::class.java)
                                    if(pillModel!!.id == pillId){
                                        oldAvailability = pillModel.availability!!
                                        Log.d("old av", oldAvailability.toString())
                                        break;


                                    }
                                }



                                Log.d("old av", oldAvailability.toString())
                                Log.d("pill",pillId )


                                newAvailability = oldAvailability.toInt()?.minus(1)!!

                                Log.d("new av minus", newAvailability.toString())

                                if(newAvailability < 5){
                                    Log.d("mnijesze od 5", newAvailability.toString())
                                    AvailabilityAlert(currentItem.name)
                                }

                                val ref = FirebaseDatabase.getInstance().getReference("Pills")

                                val updateFields: MutableMap<String, Any> = HashMap()
                                updateFields["availability"] = newAvailability

//            ref.child(pillId!!).updateChildren(updateFields)
                                ref.child(pillId).updateChildren(updateFields)



                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d("TAG", "Błąd")
                            }
                        })


                        dbReference.child("Pills_status").push().setValue(
                            mapOf(
                                "status" to currentItem.time_list!![0][1].toString(),
                                "name" to currentItem.name,
                                "date" to current,
                                "user" to uid

                            )
                        )

                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {

                                    Log.d("git", "git")
                                    val intent = Intent(context, UserScheduleActivity::class.java)
                                    context.startActivity(intent)
                                    notifyDataSetChanged()
//                                    changeDataBase("minus")


                                } else {
                                    val exception = task.exception
                                }
                            }




                    }


                if (!isChecked) {
                    val database = FirebaseDatabase.getInstance().getReference("Pills")
                    database.child(currentItem.id!!).setValue(currentItem)

                    // usunięcie z bazy danych odcheckowanej tabletki
                    val dbReference = FirebaseDatabase.getInstance().getReference().child("Pills_status")
                    val query = dbReference.orderByChild("name").equalTo(currentItem.name)
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (childSnapshot in snapshot.children) {
                                val item = childSnapshot.getValue(PillStatusModel::class.java)
                                if (item!!.date.equals(current)) {
                                    childSnapshot.ref.removeValue()

                                    var newAvailability = 0
                                    var oldAvailability = 0

                                    pillId = currentItem.id!!

                                    dbRef = FirebaseDatabase.getInstance().getReference("Pills")
                                    val userId = FirebaseAuth.getInstance().currentUser!!.uid


                                    val query = dbRef.orderByChild("pacient").equalTo(userId)
//                                    query.addValueEventListener(object : ValueEventListener {
                                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            for (snapshot in dataSnapshot.children) {
                                                val pillModel = snapshot.getValue(PillModel::class.java)
                                                if(pillModel!!.id == pillId){
                                                    oldAvailability = pillModel.availability!!
                                                    break;


                                                }
                                            }


                                            Log.d("old av", oldAvailability.toString())
                                            Log.d("pill",pillId )


                                            newAvailability = oldAvailability.toInt()?.plus(1)!!

                                            Log.d("new av plus", newAvailability.toString())


                                            if(newAvailability < 5){
                                                AvailabilityAlert(currentItem.name)
                                            }

                                            val ref = FirebaseDatabase.getInstance().getReference("Pills")

                                            val updateFields: MutableMap<String, Any> = HashMap()
                                            updateFields["availability"] = newAvailability

                                            ref.child(pillId).updateChildren(updateFields)

                                            val intent = Intent(context, UserScheduleActivity::class.java)
                                            context.startActivity(intent)
                                            notifyDataSetChanged()

                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Log.d("TAG", "Błąd")
                                        }
                                    })


                                    break
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Log.d("TAG", "Błąd")
                        }
                    })


                }
            }

            if (timesADay.size >= 2) {
                holder.checkBox2.isChecked = timesADay[1][1] as Boolean
                holder.checkBox2.setOnCheckedChangeListener { _, isChecked ->
                    currentItem.time_list!![1][1] = isChecked


                        if (isChecked) {
                            val database = FirebaseDatabase.getInstance().getReference("Pills")
                            database.child(currentItem.id!!).setValue(currentItem)

                            // Wyświetlenie powiadomienia o wzięciu tabletki
                            val context = holder.itemView.context
                            Toast.makeText(context, "Tabletka została wzięta", Toast.LENGTH_SHORT)
                                .show()


                            var newAvailability = 0
                            var oldAvailability = 0
                            Log.d("old av", oldAvailability.toString())

                            pillId = currentItem.id!!


                            dbRef = FirebaseDatabase.getInstance().getReference("Pills")
                            val userId = FirebaseAuth.getInstance().currentUser!!.uid


                            val query = dbRef.orderByChild("pacient").equalTo(userId)
                            query.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (snapshot in dataSnapshot.children) {
                                        val pillModel = snapshot.getValue(PillModel::class.java)
                                        if(pillModel!!.id == pillId){
                                            oldAvailability = pillModel.availability!!
                                            Log.d("old av", oldAvailability.toString())
                                            break;


                                        }
                                    }



                                    Log.d("old av", oldAvailability.toString())
                                    Log.d("pill",pillId )


                                    newAvailability = oldAvailability.toInt()?.minus(1)!!

                                    Log.d("new av minus", newAvailability.toString())

                                    if(newAvailability < 5){
                                        Log.d("mnijesze od 5", newAvailability.toString())
                                        AvailabilityAlert(currentItem.name)
                                    }

                                    val ref = FirebaseDatabase.getInstance().getReference("Pills")

                                    val updateFields: MutableMap<String, Any> = HashMap()
                                    updateFields["availability"] = newAvailability

//            ref.child(pillId!!).updateChildren(updateFields)
                                    ref.child(pillId).updateChildren(updateFields)



                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.d("TAG", "Błąd")
                                }
                            })

                            dbReference.child("Pills_status").push().setValue(
                                mapOf(
                                    "status" to currentItem.time_list!![1][1].toString(),
                                    "name" to currentItem.name,
                                    "date" to current,
                                    "user" to uid
                                )
                            )

                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                        Log.d("git", "git")
                                        val intent = Intent(context, UserScheduleActivity::class.java)
                                        context.startActivity(intent)
                                        notifyDataSetChanged()
//                                    changeDataBase("minus")


                                    } else {
                                        val exception = task.exception
                                    }
                                }


                        }


                    if (!isChecked) {
                        val database = FirebaseDatabase.getInstance().getReference("Pills")
                        database.child(currentItem.id!!).setValue(currentItem)

                        // usunięcie z bazy danych odcheckowanej tabletki
                        val dbReference = FirebaseDatabase.getInstance().getReference().child("Pills_status")
                        val query = dbReference.orderByChild("name").equalTo(currentItem.name)
                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (childSnapshot in snapshot.children) {
                                    val item = childSnapshot.getValue(PillStatusModel::class.java)
                                    if (item!!.date.equals(current)) {
                                        childSnapshot.ref.removeValue()

                                        var newAvailability = 0
                                        var oldAvailability = 0

                                        pillId = currentItem.id!!

                                        dbRef = FirebaseDatabase.getInstance().getReference("Pills")
                                        val userId = FirebaseAuth.getInstance().currentUser!!.uid


                                        val query = dbRef.orderByChild("pacient").equalTo(userId)
//                                    query.addValueEventListener(object : ValueEventListener {
                                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                for (snapshot in dataSnapshot.children) {
                                                    val pillModel = snapshot.getValue(PillModel::class.java)
                                                    if(pillModel!!.id == pillId){
                                                        oldAvailability = pillModel.availability!!
                                                        break;


                                                    }
                                                }


                                                Log.d("old av", oldAvailability.toString())
                                                Log.d("pill",pillId )


                                                newAvailability = oldAvailability.toInt()?.plus(1)!!

                                                Log.d("new av plus", newAvailability.toString())


                                                if(newAvailability < 5){
                                                    AvailabilityAlert(currentItem.name)
                                                }

                                                val ref = FirebaseDatabase.getInstance().getReference("Pills")

                                                val updateFields: MutableMap<String, Any> = HashMap()
                                                updateFields["availability"] = newAvailability

                                                ref.child(pillId).updateChildren(updateFields)

                                                val intent = Intent(context, UserScheduleActivity::class.java)
                                                context.startActivity(intent)
                                                notifyDataSetChanged()

                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                Log.d("TAG", "Błąd")
                                            }
                                        })


                                        break
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                Log.d("TAG", "Błąd")
                            }
                        })
                    }
                }
            }
            if (timesADay.size === 3) {
                holder.checkBox3.isChecked = timesADay[2][1] as Boolean
                holder.checkBox3.setOnCheckedChangeListener { _, isChecked ->
                    currentItem.time_list!![2][1] = isChecked


                        if (isChecked) {


                            // Wyświetlenie powiadomienia o wzięciu tabletki
                            val context = holder.itemView.context
                            Toast.makeText(context, "Tabletka została wzięta", Toast.LENGTH_SHORT)
                                .show()


                            var newAvailability = 0
                            var oldAvailability = 0
                            Log.d("old av", oldAvailability.toString())

                            pillId = currentItem.id!!


                            dbRef = FirebaseDatabase.getInstance().getReference("Pills")
                            val userId = FirebaseAuth.getInstance().currentUser!!.uid


                            val query = dbRef.orderByChild("pacient").equalTo(userId)
                            query.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (snapshot in dataSnapshot.children) {
                                        val pillModel = snapshot.getValue(PillModel::class.java)
                                        if(pillModel!!.id == pillId){
                                            oldAvailability = pillModel.availability!!
                                            Log.d("old av", oldAvailability.toString())
                                            break;


                                        }
                                    }



                                    Log.d("old av", oldAvailability.toString())
                                    Log.d("pill",pillId )


                                    newAvailability = oldAvailability.toInt()?.minus(1)!!

                                    Log.d("new av minus", newAvailability.toString())

                                    if(newAvailability < 5){
                                        Log.d("mnijesze od 5", newAvailability.toString())
                                        AvailabilityAlert(currentItem.name)
                                    }

                                    val ref = FirebaseDatabase.getInstance().getReference("Pills")

                                    val updateFields: MutableMap<String, Any> = HashMap()
                                    updateFields["availability"] = newAvailability

//            ref.child(pillId!!).updateChildren(updateFields)
                                    ref.child(pillId).updateChildren(updateFields)



                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.d("TAG", "Błąd")
                                }
                            })

                            dbReference.child("Pills_status").push().setValue(
                                mapOf(
                                    "status" to currentItem.time_list!![2][1].toString(),
                                    "name" to currentItem.name,
                                    "date" to current,
                                    "user" to uid
                                )
                            )

                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                        Log.d("git", "git")
                                        val intent = Intent(context, UserScheduleActivity::class.java)
                                        context.startActivity(intent)
                                        notifyDataSetChanged()
//                                    changeDataBase("minus")


                                    } else {
                                        val exception = task.exception
                                    }
                                }


                        }


                    if (!isChecked) {
                        val database = FirebaseDatabase.getInstance().getReference("Pills")
                        database.child(currentItem.id!!).setValue(currentItem)

                        // usunięcie z bazy danych odcheckowanej tabletki
                        val dbReference = FirebaseDatabase.getInstance().getReference().child("Pills_status")
                        val query = dbReference.orderByChild("name").equalTo(currentItem.name)
                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (childSnapshot in snapshot.children) {
                                    val item = childSnapshot.getValue(PillStatusModel::class.java)
                                    if (item!!.date.equals(current)) {
                                        childSnapshot.ref.removeValue()

                                        var newAvailability = 0
                                        var oldAvailability = 0

                                        pillId = currentItem.id!!

                                        dbRef = FirebaseDatabase.getInstance().getReference("Pills")
                                        val userId = FirebaseAuth.getInstance().currentUser!!.uid


                                        val query = dbRef.orderByChild("pacient").equalTo(userId)
//                                    query.addValueEventListener(object : ValueEventListener {
                                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                for (snapshot in dataSnapshot.children) {
                                                    val pillModel = snapshot.getValue(PillModel::class.java)
                                                    if(pillModel!!.id == pillId){
                                                        oldAvailability = pillModel.availability!!
                                                        break;


                                                    }
                                                }


                                                Log.d("old av", oldAvailability.toString())
                                                Log.d("pill",pillId )


                                                newAvailability = oldAvailability.toInt()?.plus(1)!!

                                                Log.d("new av plus", newAvailability.toString())


                                                if(newAvailability < 5){
                                                    AvailabilityAlert(currentItem.name)
                                                }

                                                val ref = FirebaseDatabase.getInstance().getReference("Pills")

                                                val updateFields: MutableMap<String, Any> = HashMap()
                                                updateFields["availability"] = newAvailability

                                                ref.child(pillId).updateChildren(updateFields)

                                                val intent = Intent(context, UserScheduleActivity::class.java)
                                                context.startActivity(intent)
                                                notifyDataSetChanged()

                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                Log.d("TAG", "Błąd")
                                            }
                                        })


                                        break
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                Log.d("TAG", "Błąd")
                            }
                        })
                    }
                }
            }


            val pillActionsButton = findViewById<ImageButton>(R.id.imageButton)
            pillActionsButton.setOnClickListener {
                val popupMenu = PopupMenu(holder.itemView.context, pillActionsButton)

                popupMenu.menuInflater.inflate(R.menu.pill_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.title) {
                        "Edytuj lek" -> {
                            val intent =
                                Intent(holder.itemView.context, EditPillActivity::class.java)
                            intent.putExtra("pillId", currentItem.id)
                            holder.itemView.context.startActivity(intent)
                            true
                        }
                        "Usuń lek" -> {
                            val pillModel = pillList?.get(position)
                            val pillId = pillModel?.id
                            if (pillId != null) {
                                val dbRef = FirebaseDatabase.getInstance().getReference("Pills").child(pillId)
                                dbRef.removeValue()
                            }
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()

            }


        }


    }
    private fun getBasicAmoutFromDb(currentPillName: String, callback: (basicAvailability: String) -> Unit) {
        dbRef = FirebaseDatabase.getInstance().getReference("Pills")
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        val query = dbRef.orderByChild("user").equalTo(userId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val pillModel = snapshot.getValue(PillModel::class.java)
                    if (pillModel!!.name == currentPillName) {
                        basicAvailability = pillModel.availability
                        // Wywołaj callback, przekazując wartość basicAvailability
                        callback.invoke(basicAvailability.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
    }

//    private fun getAvailabilityFromDb(currentPillName: String, basicAvailability: String) {
//        dbRef = FirebaseDatabase.getInstance().getReference("Pills_status")
//        val userId = FirebaseAuth.getInstance().currentUser!!.uid
//
//        val pillStatusList: MutableList<PillStatusModel> = mutableListOf()
//
//        Log.d("current pill", currentPillName)
//
//        val query = dbRef.orderByChild("user").equalTo(userId)
//        query.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                pillStatusList.clear()
//                for (snapshot in dataSnapshot.children) {
//                    val pillStatus = snapshot.getValue(PillStatusModel::class.java)
//                    if (pillStatus!!.name == currentPillName) {
//                        pillStatusList.add(pillStatus!!)
//                    }
//                }
//
//                Log.d("pills status list", pillStatusList.toString())
//
//                // Sprawdź, czy lista nie jest pusta, a następnie pobierz ostatni element
//                if (pillStatusList.isNotEmpty()) {
//                    val lastPillStatus = pillStatusList.last()
//                    availabilityPill = lastPillStatus.availability
//                    Log.d("Ostatni element", lastPillStatus.toString())
//                } else {
//                    // Użyj przekazanej wartości basicAvailability
//                    availabilityPill = basicAvailability
//                }
//
//                Log.d("availabilioty pill", availabilityPill)
//
//            }
//
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("TAG", "Błąd")
//            }
//        })
//    }


//    private fun getAvailabilityFromDb(currentPillName: String) {
//        dbRef = FirebaseDatabase.getInstance().getReference("Pills_status")
//        val userId = FirebaseAuth.getInstance().currentUser!!.uid
//
//        val pillStatusList: MutableList<PillStatusModel> = mutableListOf()
//
//        Log.d("current pill", currentPillName)
//
//        val query = dbRef.orderByChild("user").equalTo(userId)
//        query.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                pillStatusList.clear()
//                for (snapshot in dataSnapshot.children) {
//                    val pillStatus = snapshot.getValue(PillStatusModel::class.java)
//                    if(pillStatus!!.name == currentPillName){
//                        pillStatusList.add(pillStatus!!)
//
//                    }
//                }
//
//                Log.d("pills status list", pillStatusList.toString())
//
//                // Sprawdź, czy lista nie jest pusta, a następnie pobierz ostatni element
//                if (pillStatusList.isNotEmpty()) {
//                    val lastPillStatus = pillStatusList.last()
//                    availability = lastPillStatus.availability
//                    Log.d("Ostatni element", lastPillStatus.toString())
//                }else{
//                    availability = basicAvailability.toString()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("TAG", "Błąd")
//            }
//        })
//    }
//
    private fun getBasicAmoutFromDb(currentPillName: String) {
        dbRef = FirebaseDatabase.getInstance().getReference("Pills")
        val userId = FirebaseAuth.getInstance().currentUser!!.uid


        val query = dbRef.orderByChild("user").equalTo(userId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val pillModel = snapshot.getValue(PillModel::class.java)
                    if(pillModel!!.name == currentPillName){
                        basicAvailability = pillModel.availability


                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Błąd")
            }
        })
    }



//
//    @RequiresApi(Build.VERSION_CODES.O)
//    public fun changeDataBase(change: String){
//
//        while(counter == 1) {
//
//            var newAvailability = 0
//
//
//            if (change == "minus") {
//                Log.d("minus", "minus")
//                newAvailability = oldAvailability?.minus(1)
//                    ?: 0 // Jeśli oldAvailability jest null, przyjmujemy wartość 0
//
//            } else if (change == "plus") {
//                Log.d("plus", "plus")
//                newAvailability = oldAvailability?.plus(1)
//                    ?: 0 // Jeśli oldAvailability jest null, przyjmujemy wartość 0
//
//            }
//
//            if(newAvailability < 5){
//                AvailabilityAlert()
//            }
//
//            val ref = FirebaseDatabase.getInstance().getReference("Pills")
//
//            val updateFields: MutableMap<String, Any> = HashMap()
//            updateFields["availability"] = newAvailability
//
////            ref.child(pillId!!).updateChildren(updateFields)
//            ref.child(pillId).updateChildren(updateFields)
//
//
//            counter += 1
//        }
//
//    }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun AvailabilityAlert(pillName: String) {

        val dbFirebase = FirebaseDatabase.getInstance()
        val dbReference = dbFirebase.getReference()

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        val id = UUID.randomUUID().toString()



        Log.d("alert to pilll name", pillName)


        val message = "Uwaga, kończą się tabletki w opakowaniu dla leku $pillName!"

        val currentDate = LocalDate.now().toString()

        // Sprawdź, czy powiadomienie dla tego leku w bieżącym dniu już zostało wysłane
        val notificationsQuery = dbReference.child("Notifications")
            .orderByChild("pill")
            .equalTo(pillName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var shouldSendNotification = true

                    for (snapshot in dataSnapshot.children) {
                        val notificationDate = snapshot.child("date").getValue(String::class.java)
                        val notificationPill = snapshot.child("pill").getValue(String::class.java)
                        val notificationPacient = snapshot.child("pacient").getValue(String::class.java)


                        if (notificationDate == currentDate && notificationPacient == uid ) {
                            shouldSendNotification = false
                            break
                        }
                    }

                    if (shouldSendNotification) {
                        // Wyślij powiadomienie
                        dbReference.child("Notifications").push().setValue(
                            mapOf(
                                "message" to message,
                                "pill" to pillName,
                                "date" to currentDate,
                                "pacient" to uid as Any,
                                "id" to id as Any
                            )
                        )
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("git", "git")
                                } else {
                                    val exception = task.exception
                                }
                            }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Obsłuż błąd odczytu danych
                }
            })
    }






    override fun getItemCount(): Int = pillList?.size ?: 0

    class PillItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pillTitle: TextView = itemView.findViewById(R.id.pillTitle)
        val time1: TextView = itemView.findViewById(R.id.pillHour1)
        val time2: TextView = itemView.findViewById(R.id.pillHour2)
        val time3: TextView = itemView.findViewById(R.id.pillHour3)
        val checkBox1: CheckBox = itemView.findViewById(R.id.cbDone1)
        val checkBox2: CheckBox = itemView.findViewById(R.id.cbDone2)
        val checkBox3: CheckBox = itemView.findViewById(R.id.cbDone3)
    }



}
