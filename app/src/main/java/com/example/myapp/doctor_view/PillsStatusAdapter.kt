package com.example.myapp.doctor_view


import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.SharedObject

class PillsStatusAdapter(private var ValuesArray: MutableMap<String, String>) : RecyclerView.Adapter<PillsStatusAdapter.ViewHolder>()
{

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val nameTextView1 = itemView.findViewById<TextView>(R.id.datePill)
        val nameTextView2 = itemView.findViewById<TextView>(R.id.countPill)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillsStatusAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.pills_status_item, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }



    override fun onBindViewHolder(viewHolder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        // Get the data model based on position
//        val elem: String? = ValuesArray.



        val keyAtIndex = ValuesArray.keys.elementAt(position)
        val valueAtIndex = ValuesArray[keyAtIndex]

        val textView1 = viewHolder.nameTextView1
        textView1.text = keyAtIndex
        val textView2 = viewHolder.nameTextView2
        textView2.text = valueAtIndex







    }


    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return ValuesArray.size
    }

//    fun returnValuesArray(): Array<Value> {
//        return ValuesArray.toTypedArray()
//    }


}