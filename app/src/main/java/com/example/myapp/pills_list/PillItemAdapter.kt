package com.example.myapp.pills_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R

class PillItemAdapter (
    private val pillList: ArrayList<PillItem>): RecyclerView.Adapter<PillItemAdapter.PillItemViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_pill, parent, false)
        return PillItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PillItemViewHolder, position: Int) {
        val currentItem = pillList[position]
        holder.hour.text = currentItem.hour.toString()
        holder.pillTitle.text = currentItem.name
    }

    override fun getItemCount(): Int = pillList.size

    class PillItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pillTitle: TextView = itemView.findViewById(R.id.pillTitle)
        val hour: TextView = itemView.findViewById(R.id.pillHour)
    }
}