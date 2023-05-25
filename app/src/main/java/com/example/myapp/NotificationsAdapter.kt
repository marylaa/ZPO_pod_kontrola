package com.example.myapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.report.Value


class NotificationsAdapter(private val ValuesArray: MutableList<NotificationModel>) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>()
{

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val nameTextView1 = itemView.findViewById<TextView>(R.id.messageDate)
        val nameTextView2 = itemView.findViewById<TextView>(R.id.message)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.notification_item, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }



    override fun onBindViewHolder(viewHolder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        // Get the data model based on position
        val elem: NotificationModel = ValuesArray[position]


        // Set item views based on your views and data model
        val textView1 = viewHolder.nameTextView1
//        textView1.text = contact.name
        textView1.text = elem.date
        val textView2 = viewHolder.nameTextView2
//        textView2.text = contact.unit
        textView2.text = elem.message




    }



    override fun getItemCount(): Int {
        return ValuesArray.size
    }




}