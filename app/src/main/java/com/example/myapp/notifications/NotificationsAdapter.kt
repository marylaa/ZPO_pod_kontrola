package com.example.myapp.notifications

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.google.firebase.database.FirebaseDatabase


class NotificationsAdapter(
    private val messagesList: MutableList<NotificationModelAlert>,
    private val context: Context
) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView1 = itemView.findViewById<TextView>(R.id.messageDate)
        val nameTextView2 = itemView.findViewById<TextView>(R.id.message)
        val deleteButton = itemView.findViewById<ImageButton>(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = messagesList!![position]

        holder.nameTextView1.text = currentItem.date
        holder.nameTextView2.text = currentItem.message

        holder.deleteButton.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context)
                .setTitle("Potwierdzenie")
                .setMessage("Czy na pewno chcesz usunąć wiadomość?")
                .setPositiveButton("Tak") { _, _ ->
                    val dbRef = FirebaseDatabase.getInstance().getReference("Notifications")
                    dbRef.child(currentItem.id!!).removeValue()
                    Toast.makeText(context, "Wiadomość została usunięta", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Anuluj") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            alertDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }
}