package com.example.myapp.doctor_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R

class RecycleViewAdapterItem(
    private val context: Context,
    private val mDate: List<String>,
    private val mDoctors: List<String>
) : RecyclerView.Adapter<RecycleViewAdapterItem.ViewHolder>() {

    private var mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the custom layout
        val contactView = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_monthly, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = mDate[position]
        val doctor = mDoctors[position]
        holder.myTextView.text = date
        holder.myTextView1.text = doctor
    }

    override fun getItemCount(): Int {
        return mDate.size
    }

    fun getItem(id: Int): String {
        return mDate[id]
    }

    fun setClickListener(itemClickListener: ItemClickListener) {
        mClickListener = itemClickListener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val myView: View = itemView
        val myTextView: TextView = itemView.findViewById(R.id.contact_name1)
        val myTextView1: TextView = itemView.findViewById(R.id.contact_name2)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            mClickListener?.onItemClick(view, adapterPosition)
        }
    }

    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}
