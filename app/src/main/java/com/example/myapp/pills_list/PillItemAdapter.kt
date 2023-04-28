package com.example.myapp.pills_list

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.google.firebase.database.FirebaseDatabase

class PillItemAdapter(private val pillList: MutableList<PillModel>?): RecyclerView.Adapter<PillItemAdapter.PillItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_pill, parent, false)
        return PillItemViewHolder(itemView)
    }

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
            val pillActionsButton = findViewById<ImageButton>(R.id.imageButton)
            pillActionsButton.setOnClickListener {
                val popupMenu = PopupMenu(holder.itemView.context, pillActionsButton)

                popupMenu.menuInflater.inflate(R.menu.pill_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.title) {
                        "Edytuj lek" -> {
                            val intent = Intent(holder.itemView.context, EditPillActivity::class.java)
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

    override fun getItemCount(): Int = pillList?.size ?: 0

    class PillItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pillTitle: TextView = itemView.findViewById(R.id.pillTitle)
        val time: TextView = itemView.findViewById(R.id.pillHour)
    }
}
