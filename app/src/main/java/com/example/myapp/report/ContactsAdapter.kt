package com.example.myapp.report

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R

class ContactsAdapter (private val ValuesArray: List<Value>) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>()
{


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val nameTextView1 = itemView.findViewById<TextView>(R.id.contact_name1)
        val nameTextView2 = itemView.findViewById<TextView>(R.id.contact_name2)
        val nameTextView3 = itemView.findViewById<EditText>(R.id.contact_name3)
        val button = itemView.findViewById<Button>(R.id.button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.contact, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        // Get the data model based on position
        val elem: Value = ValuesArray[position]

        // Set item views based on your views and data model
        val textView1 = viewHolder.nameTextView1
        textView1.text = elem.getName()
        val textView2 = viewHolder.nameTextView2
        textView2.text = elem.getUnit()

        val textView3 = viewHolder.nameTextView3
        textView3.setInputType(InputType.TYPE_CLASS_NUMBER)
        textView3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                val context = viewHolder.itemView.context
                if (viewHolder.adapterPosition != RecyclerView.NO_POSITION) {
                    val currentPos = viewHolder.adapterPosition
                    var inputValue = editable.toString().toDoubleOrNull() // Change to toDoubleOrNull

                    var isValueChanged = false // Dodatkowa zmienna logiczna

                    when (currentPos) {
                        1, 3 -> {
//                            val inputValue = editable.toString().toDoubleOrNull()
                            if (inputValue != null && inputValue > 24) {
                                Toast.makeText(context, "Wprowadź wartość mniejszą lub równą 24", Toast.LENGTH_SHORT).show()
                                textView3.setText("24")
                            } else {
                                ValuesArray[currentPos].setInput((inputValue ?: 0.0).toString())
                            }
                        }
                        2 -> {
//                            val inputValue = editable.toString().toIntOrNull()
                            if (inputValue != null && inputValue > 160) {
                                Toast.makeText(context, "Wprowadź wartość mniejszą lub równą 160", Toast.LENGTH_SHORT).show()
                                textView3.setText("70")
                            } else {
                                ValuesArray[currentPos].setInput((inputValue ?: 0.0).toString())
                            }
                        }
                        4 -> {
//                            val inputValue = editable.toString().toDoubleOrNull()
                            val isDefaultValue = (inputValue == null || inputValue <= 45) // Sprawdzamy, czy wartość jest domyślna lub mniejsza niż 45
                            if (isDefaultValue) {
                                ValuesArray[currentPos].setInput((inputValue ?: 0.0).toString())
                            } else {
                                Toast.makeText(context, "Wprowadź wartość mniejszą lub równą 45", Toast.LENGTH_SHORT).show()
                                textView3.setText("36,6")
                            }
                        }
                        0 -> {
//                            val inputValue = editable.toString().toIntOrNull()
                            val isDefaultValue = (inputValue == null || (inputValue <= 200))
                            if (isDefaultValue && !isValueChanged) { // Sprawdzamy, czy wartość domyślna nie została jeszcze zmieniona
                                ValuesArray[currentPos].setInput((inputValue ?: 0.0).toString())
                            } else {
                                Toast.makeText(context, "Wprowadź wartość pomiędzy 100 a 200", Toast.LENGTH_SHORT).show()
                                textView3.setText("120")
                                isValueChanged = true // Ustawiamy flagę isValueChanged na true po wprowadzeniu niepoprawnej wartości
                            }
                        }
                        5 -> {
//                            val inputValue = editable.toString().toIntOrNull()
                            val isDefaultValue = (inputValue == null || (inputValue <= 150))
                            if (isDefaultValue) {
                                ValuesArray[currentPos].setInput((inputValue ?: 0.0).toString())
                            } else {
                                Toast.makeText(context, "Wprowadź wartość pomiędzy 50 a 150", Toast.LENGTH_SHORT).show()
                                textView3.setText("80")
                            }
                        }
                        else -> {
                            ValuesArray[currentPos].setInput(editable.toString())
                        }
                    }
                    Log.d(TAG, editable.toString())

                    for (elem in ValuesArray) {
                        Log.d(TAG, elem.printVal())
                    }
                }
            }
        })
        val button = viewHolder.button
    }

    override fun getItemCount(): Int {
        return ValuesArray.size
    }

    fun returnValuesArray(): Array<Value> {
        return ValuesArray.toTypedArray()
    }
}