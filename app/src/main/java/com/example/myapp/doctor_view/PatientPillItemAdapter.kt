package com.example.myapp.doctor_view

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.pills_list.PillModel
import com.example.myapp.pills_list.PillModelCustom
import com.google.firebase.database.FirebaseDatabase

class PatientPillItemAdapter(private val pillList: MutableList<Any>?, private val context: Context): RecyclerView.Adapter<PatientPillItemAdapter.PillItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_pill_doctor_view, parent, false)
        return PillItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PillItemViewHolder, position: Int) {
        val currentItem = pillList!![position]

        if (currentItem is PillModel) {
            holder.pillTitle.text = currentItem.name

            holder.itemView.apply {
                val pillActionsButton = findViewById<ImageButton>(R.id.imageButton)
                pillActionsButton.setOnClickListener {
                    val popupMenu = PopupMenu(holder.itemView.context, pillActionsButton)

                    popupMenu.menuInflater.inflate(R.menu.pill_menu, popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.title) {
                            "Edytuj lek" -> {
                                val intent = Intent(holder.itemView.context, DoctorEditPillActivity::class.java)
                                intent.putExtra("pillId", currentItem.id)
                                intent.putExtra("patientId", currentItem.pacient)
                                holder.itemView.context.startActivity(intent)
                                true
                            }
                            "Usuń lek" -> {
                                val alertDialog = AlertDialog.Builder(context)
                                    .setTitle("Potwierdzenie")
                                    .setMessage("Czy na pewno chcesz usunąć lek?")
                                    .setPositiveButton("Tak") { _, _ ->
                                        val pillId = currentItem.id
                                        val dbRef = FirebaseDatabase.getInstance().getReference("Pills").child(
                                            pillId!!
                                        )
                                        dbRef.removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(context, "Tabletka została usunięta", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Wystąpił błąd", Toast.LENGTH_SHORT).show()
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
        } else if(currentItem is PillModelCustom) {
            holder.pillTitle.text = currentItem.name

            holder.itemView.apply {
                val pillActionsButton = findViewById<ImageButton>(R.id.imageButton)
                pillActionsButton.setOnClickListener {
                    val popupMenu = PopupMenu(holder.itemView.context, pillActionsButton)

                    popupMenu.menuInflater.inflate(R.menu.pill_menu, popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.title) {
                            "Edytuj lek" -> {
                                val intent = Intent(holder.itemView.context, DoctorEditPillActivity::class.java)
                                intent.putExtra("pillId", currentItem.id)
                                intent.putExtra("patientId", currentItem.pacient)
                                holder.itemView.context.startActivity(intent)
                                true
                            }
                            "Usuń lek" -> {
                                val alertDialog = AlertDialog.Builder(context)
                                    .setTitle("Potwierdzenie")
                                    .setMessage("Czy na pewno chcesz usunąć lek?")
                                    .setPositiveButton("Tak") { _, _ ->
                                        val pillId = currentItem.id
                                        val dbRef = FirebaseDatabase.getInstance().getReference("Pills").child(
                                            pillId!!
                                        )
                                        dbRef.removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(context, "Tabletka została usunięta", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Wystąpił błąd", Toast.LENGTH_SHORT).show()
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
            }        }
    }


    override fun getItemCount(): Int = pillList?.size ?: 0

    class PillItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pillTitle: TextView = itemView.findViewById(R.id.pillTitle)
    }
}
