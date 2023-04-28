package com.example.myapp.pacients_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.login.UserModel

class PacientItemAdapter (
    private val pacientList: MutableList<UserModel>? ): RecyclerView.Adapter<PacientItemAdapter.PatientItemViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.pacient_item, parent, false)
        return PatientItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PatientItemViewHolder, position: Int) {
        val currentItem = pacientList!![position]
        holder.pacientTitle.text = currentItem.firstName + " " + currentItem.lastName
    }

    override fun getItemCount(): Int = pacientList!!.size

    class PatientItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pacientTitle: TextView = itemView.findViewById(R.id.pacientTitle)
    }
}