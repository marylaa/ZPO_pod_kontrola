package com.example.myapp.pacients_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R

class PacientItemAdapter (
    private val pacientList: MutableList<PacientModel>? ): RecyclerView.Adapter<PacientItemAdapter.PillItemViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.pacient_item, parent, false)
        return PillItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PillItemViewHolder, position: Int) {
        val currentItem = pacientList!![position]
        holder.pacientTitle.text = currentItem.firstName + currentItem.lastName

    }

    override fun getItemCount(): Int = pacientList!!.size

    class PillItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pacientTitle: TextView = itemView.findViewById(R.id.pacientTitle)
    }
}