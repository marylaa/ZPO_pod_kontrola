package com.example.myapp.doctor_view

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.pills_list.PillModel
import com.google.firebase.database.FirebaseDatabase

class PatientPillItemAdapter(private val pillList: MutableList<PillModel>?): RecyclerView.Adapter<PatientPillItemAdapter.PillItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_pill_doctor_view, parent, false)
        return PillItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PillItemViewHolder, position: Int) {
        val currentItem = pillList!![position]
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

    override fun getItemCount(): Int = pillList?.size ?: 0

    class PillItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pillTitle: TextView = itemView.findViewById(R.id.pillTitle)
    }
}
