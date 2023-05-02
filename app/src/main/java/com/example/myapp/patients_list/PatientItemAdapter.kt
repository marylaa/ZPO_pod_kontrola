package com.example.myapp.patients_list

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.doctor_view.PatientActionsActivity
import com.example.myapp.R
import com.example.myapp.login.UserModel

class PatientItemAdapter(private val pacientList: MutableList<UserModel>?) :
    RecyclerView.Adapter<PatientItemAdapter.PatientItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.patient_item, parent, false)
        return PatientItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PatientItemViewHolder, position: Int) {
        val currentItem = pacientList!![position]
        holder.pacientTitle.text = currentItem.firstName + " " + currentItem.lastName

        holder.pacientId = currentItem.id
        holder.choosePacientButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, PatientActionsActivity::class.java)
            intent.putExtra("patientId", holder.pacientId)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = pacientList!!.size

    class PatientItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pacientTitle: TextView = itemView.findViewById(R.id.pacientTitle)
        val choosePacientButton: ImageButton = itemView.findViewById(R.id.choosePacient)
        var pacientId: String? = null
    }
}
