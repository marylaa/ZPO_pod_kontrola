package com.example.myapp.pills_list

import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class PillNextDayItemAdapter(private val pillList: MutableList<Any>?): RecyclerView.Adapter<PillNextDayItemAdapter.PillItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_pill, parent, false)
        return PillItemViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: PillItemViewHolder, position: Int) {
        val item = pillList!![position]

        if (item is PillModel) {
            val currentItem = item as PillModel
            var timesADay = currentItem.time_list!!
            holder.time1.text = timesADay[0]?.get(0)?.toString()
            if (timesADay.size === 2) {
                holder.checkBox2.setVisibility(View.VISIBLE);
                holder.time2.setVisibility(View.VISIBLE);
                holder.time2.text = timesADay[1]?.get(0)?.toString()
            } else if (timesADay.size === 3) {
                holder.checkBox2.setVisibility(View.VISIBLE);
                holder.time2.setVisibility(View.VISIBLE);
                holder.time2.text = timesADay[1]?.get(0)?.toString()
                holder.checkBox3.setVisibility(View.VISIBLE);
                holder.time3.setVisibility(View.VISIBLE);
                holder.time3.text = timesADay[2]?.get(0)?.toString()
            }

            holder.pillTitle.text = currentItem.name

            holder.itemView.apply {
                holder.checkBox1.isChecked = false
                holder.checkBox1.isEnabled = false

                if (timesADay.size >= 2) {
                    holder.checkBox2.isChecked = false
                    holder.checkBox2.isEnabled = false
                }
                if (timesADay.size === 3) {
                    holder.checkBox3.isChecked = false
                    holder.checkBox3.isEnabled = false
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
                                val alertDialog = AlertDialog.Builder(context)
                                    .setTitle("Potwierdzenie")
                                    .setMessage("Czy na pewno chcesz usunąć lek?")
                                    .setPositiveButton("Tak") { _, _ ->
                                        val pillModel = pillList?.get(position)
                                        val pill = pillModel as PillModel
                                        val pillId = pill?.id
                                        if (pillId != null) {
                                            val dbRef =
                                                FirebaseDatabase.getInstance().getReference("Pills")
                                                    .child(pillId)
                                            dbRef.removeValue().addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(
                                                        context,
                                                        "Tabletka została usunięta",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Wystąpił błąd",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                    }.setNegativeButton("Anuluj") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .create()

                                alertDialog.show()
                                true
                            }
                            else -> false
                        }
                    }
                    popupMenu.show()

                }
            }
        } else if (item is PillModelCustom) {
            val currentItem = item as PillModelCustom

            val days = arrayOf("Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela")
            val today = LocalDate.now()
            val dayOfWeek = today.dayOfWeek.value
            val tomorrowDay = days.get(dayOfWeek)

            var timesADay: List<List<Any>>? = listOf()
            for (item in currentItem?.time_list.orEmpty()) {
                val dayValue = item["day"]

                // sprawdzenie czy jutro bedzie brana tabletka
                if (dayValue.toString().equals(tomorrowDay)) {
                    timesADay = item["times"] as? List<List<Any>>
                }
            }

            holder.time1.text = timesADay?.get(0)?.get(0)?.toString()
            if (timesADay?.size === 2) {
                holder.checkBox2.setVisibility(View.VISIBLE);
                holder.time2.setVisibility(View.VISIBLE);
                holder.time2.text = timesADay[1]?.get(0)?.toString()
            } else if (timesADay?.size === 3) {
                holder.checkBox2.setVisibility(View.VISIBLE);
                holder.time2.setVisibility(View.VISIBLE);
                holder.time2.text = timesADay[1]?.get(0)?.toString()
                holder.checkBox3.setVisibility(View.VISIBLE);
                holder.time3.setVisibility(View.VISIBLE);
                holder.time3.text = timesADay[2]?.get(0)?.toString()
            }

            holder.pillTitle.text = currentItem.name

            holder.itemView.apply {
                holder.checkBox1.isChecked = false
                holder.checkBox1.isEnabled = false

                if (timesADay?.size!! >= 2) {
                    holder.checkBox2.isChecked = false
                    holder.checkBox2.isEnabled = false
                }
                if (timesADay?.size === 3) {
                    holder.checkBox3.isChecked = false
                    holder.checkBox3.isEnabled = false
                }

                val pillActionsButton = findViewById<ImageButton>(R.id.imageButton)
                pillActionsButton.setOnClickListener {
                    val popupMenu = PopupMenu(holder.itemView.context, pillActionsButton)

                    popupMenu.menuInflater.inflate(R.menu.pill_menu, popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.title) {
                            "Edytuj lek" -> {
                                val intent =
                                    Intent(
                                        holder.itemView.context,
                                        EditPillActivity::class.java
                                    )
                                intent.putExtra("pillId", currentItem.id)
                                holder.itemView.context.startActivity(intent)
                                true
                            }
                            "Usuń lek" -> {
                                val alertDialog = AlertDialog.Builder(context)
                                    .setTitle("Potwierdzenie")
                                    .setMessage("Czy na pewno chcesz usunąć lek?")
                                    .setPositiveButton("Tak") { _, _ ->
                                        val pillModel = pillList?.get(position)
                                        val pill = pillModel as PillModelCustom
                                        val pillId = pill?.id
                                        if (pillId != null) {
                                            val dbRef =
                                                FirebaseDatabase.getInstance().getReference("Pills")
                                                    .child(pillId)
                                            dbRef.removeValue().addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(
                                                        context,
                                                        "Tabletka została usunięta",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Wystąpił błąd",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                    }.setNegativeButton("Anuluj") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .create()

                                alertDialog.show()
                                true
                            }
                            else -> false
                        }
                    }
                    popupMenu.show()

                }
            }
        }
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
