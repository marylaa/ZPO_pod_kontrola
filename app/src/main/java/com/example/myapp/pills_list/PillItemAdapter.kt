package com.example.myapp.pills_list

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
        var hour: String? = currentItem.hour.toString()
        var minute: String? = currentItem.minute.toString()

        if (currentItem.hour!! < 10) {
            hour = "0" + currentItem.hour.toString()
        }
        if (currentItem.minute!! < 10) {
            minute = "0" + currentItem.minute.toString()
        }
        holder.time.text = hour + ":" + minute
        holder.pillTitle.text = currentItem.name



        holder.itemView.apply {
            val checkBox = findViewById<CheckBox>(R.id.cbDone)
            checkBox.isChecked = currentItem.isChecked
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                currentItem.isChecked = isChecked

                val dbFirebase = FirebaseDatabase.getInstance()
                val dbReference = dbFirebase.getReference()

                val user = FirebaseAuth.getInstance().currentUser;
                val uid = user?.uid

                val current = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val formattedDate = current.format(formatter)
                val dateTime = LocalDate.parse(formattedDate, formatter)





                dbReference.child("pills_status").push().setValue(
                    mapOf(
                        "Status" to currentItem.isChecked.toString(),
                        "Nazwa" to currentItem.getName().toString(),
                        "Data" to dateTime.toString(),
                        "user" to uid
                    )
                )

                if (isChecked) {
                    // Usunięcie obiektu z listy po zaznaczeniu CheckBoxa
                    pillList.removeAt(position)
                    notifyDataSetChanged()

                    // Wyświetlenie powiadomienia o wzięciu tabletki
                    val context = holder.itemView.context
                    Toast.makeText(context, "Tabletka została wzięta", Toast.LENGTH_SHORT).show()

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
