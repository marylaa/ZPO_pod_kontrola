package com.example.myapp.pills_list

import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class PillItemAdapter(private val pillList: MutableList<PillModel>?): RecyclerView.Adapter<PillItemAdapter.PillItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_pill, parent, false)
        return PillItemViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: PillItemViewHolder, position: Int) {
        val currentItem = pillList!![position]

        holder.time.text = currentItem.time_list!!.get(0).get(0)
            .toString() //na razie na sztywno 0 pozniej trzeba bedzie to zmieniac
        holder.pillTitle.text = currentItem.name



        holder.itemView.apply {
//            holder.checkBox.isChecked = currentItem.isChecked
//            holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
//                currentItem.isChecked = isChecked
//
//                val dbFirebase = FirebaseDatabase.getInstance()
//                val dbReference = dbFirebase.getReference()
//
//                val user = FirebaseAuth.getInstance().currentUser;
//                val uid = user?.uid
//
//                val current = LocalDate.now()
//                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//                val formattedDate = current.format(formatter)
//                val dateTime = LocalDate.parse(formattedDate, formatter)
//
//                if (isChecked) {
//                    val database = FirebaseDatabase.getInstance().getReference("Pills")
//                    val updateData = hashMapOf<String, Any>("checked" to true)
//
//                    database.child(currentItem.id!!).updateChildren(updateData)
//                        .addOnFailureListener {
//                            Log.d("TAG", "Błąd")
//                        }
//
//                    // Wyświetlenie powiadomienia o wzięciu tabletki
//                    val context = holder.itemView.context
//                    Toast.makeText(context, "Tabletka została wzięta", Toast.LENGTH_SHORT).show()
//
//                    dbReference.child("pills_status").push().setValue(
//                        mapOf(
//                            "Status" to currentItem.isChecked.toString(),
//                            "Nazwa" to currentItem.name,
//                            "Data" to dateTime.toString(),
//                            "User" to uid
//                        )
//                    )
//                }
//
//                if (!isChecked) {
//                    val database = FirebaseDatabase.getInstance().getReference("Pills")
//                    val updateData = hashMapOf<String, Any>("checked" to false)
//
//                    database.child(currentItem.id!!).updateChildren(updateData)
//                        .addOnFailureListener {
//                            Log.d("TAG", "Błąd")
//                        }
//
//                    val dbReference = FirebaseDatabase.getInstance().getReference().child("pills_status")
//                    val query = dbReference.orderByChild("Nazwa").equalTo(currentItem.name)
//                    query.addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            for (childSnapshot in snapshot.children) {
//                                val item = childSnapshot.getValue(PillStatusModel::class.java)
//                                if (item!!.Data.equals(dateTime.toString())) {
//                                    childSnapshot.ref.removeValue()
//                                    break
//                                }
//                            }
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            Log.d("TAG", "Błąd")
//                        }
//                    })
//                }
//            }

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
                                val dbRef = FirebaseDatabase.getInstance().getReference("Pills")
                                    .child(pillId)
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

    override fun getItemCount(): Int = pillList?.size ?: 0

    class PillItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pillTitle: TextView = itemView.findViewById(R.id.pillTitle)
        val time: TextView = itemView.findViewById(R.id.pillHour)
        val checkBox: CheckBox = itemView.findViewById(R.id.cbDone)
    }
}
