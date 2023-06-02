package com.example.myapp.patients_list

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.doctor_view.PatientActionsActivity
import com.example.myapp.R
import com.example.myapp.login.UserModel
import com.example.myapp.pills_list.EditPillActivity
import com.google.firebase.database.FirebaseDatabase

class PatientItemAdapter(private val patientList: MutableList<UserModel>?) :
    RecyclerView.Adapter<PatientItemAdapter.PatientItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.patient_item, parent, false)
        return PatientItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PatientItemViewHolder, position: Int) {
        val currentItem = patientList!![position]
        holder.patientTitle.text = currentItem.firstName + " " + currentItem.lastName

        holder.patientId = currentItem.id
        holder.choosePatientButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, PatientActionsActivity::class.java)
            intent.putExtra("patientId", holder.patientId)
            holder.itemView.context.startActivity(intent)
        }

//        holder.itemView.apply {
//            val pillActionsButton = findViewById<ImageButton>(R.id.more)
//            pillActionsButton.setOnClickListener {
//                val popupMenu = PopupMenu(holder.itemView.context, pillActionsButton)
//
//                popupMenu.menuInflater.inflate(R.menu.pill_menu, popupMenu.menu)
//                popupMenu.setOnMenuItemClickListener { menuItem ->
//                    when (menuItem.title) {
//                        "Edytuj lek" -> {
//                            val intent =
//                                Intent(holder.itemView.context, EditPillActivity::class.java)
//                            intent.putExtra("pillId", currentItem.id)
//                            holder.itemView.context.startActivity(intent)
//                            true
//                        }
//                        "Usuń lek" -> {
//                            val alertDialog = AlertDialog.Builder(context)
//                                .setTitle("Potwierdzenie")
//                                .setMessage("Czy na pewno chcesz usunąć lek?")
//                                .setPositiveButton("Tak") { _, _ ->
//                                    val pillModel = pillList?.get(position)
//                                    val pillId = pillModel?.id
//                                    if (pillId != null) {
//                                        val dbRef =
//                                            FirebaseDatabase.getInstance().getReference("Pills")
//                                                .child(pillId)
//                                        dbRef.removeValue()
//                                        Toast.makeText(
//                                            context,
//                                            "Tabletka została usunięta",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
//                                }.setNegativeButton("Anuluj") { dialog, _ ->
//                                    dialog.dismiss()
//                                }
//                                .create()
//
//                            alertDialog.show()
//                            true
//                        }
//                        else -> false
//                    }
//                }
//                popupMenu.show()
//            }
//        }
    }

    override fun getItemCount(): Int = patientList!!.size

    class PatientItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val patientTitle: TextView = itemView.findViewById(R.id.pacientTitle)
        val choosePatientButton: ImageButton = itemView.findViewById(R.id.choosePacient)
        var patientId: String? = null
    }
}
