package com.example.myapp.monthly_report

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class RecycleViewAdapter(
    private val context: Context,
    private val viewColors: List<Int>,
    private val dates: List<LocalDate>
) : RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>() {

    private var mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mClickListener: ItemClickListener? = null

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.recycleview_item, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the view and textview in each row
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val color = viewColors[position]
        val date = dates[position]
        holder.myView.setBackgroundColor(color)
        val formatter = DateTimeFormatter.ofPattern("d MMMM", Locale("pl"))
        val formattedDate = date.format(formatter)
        holder.myTextView.text = formattedDate
    }

    // total number of rows
    override fun getItemCount(): Int {
        return dates.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var myView: View = itemView.findViewById(R.id.colorView)
        var myTextView: TextView = itemView.findViewById(R.id.tvAnimalName)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            mClickListener?.onItemClick(view, adapterPosition)
        }
    }

    // convenience method for getting data at click position
    fun getItem(id: Int): String {
        return dates[id].toString()
    }

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }
}
