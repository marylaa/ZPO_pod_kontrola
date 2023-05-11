package com.example.myapp.report

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R

class ContactsAdapter (private val ValuesArray: List<Value>) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>()
{
//class ContactsAdapter: RecyclerView.Adapter<ContactsAdapter.ViewHolder>()
//{
//class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {


//    var ValuesArray = ArrayList<Value>()
//
//
//    constructor(valuesArray: ArrayList<Value>){
//        this.ValuesArray = valuesArray
//    }
//




    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val nameTextView1 = itemView.findViewById<TextView>(R.id.contact_name1)
        val nameTextView2 = itemView.findViewById<TextView>(R.id.contact_name2)
        val nameTextView3 = itemView.findViewById<TextView>(R.id.contact_name3)
        val button = itemView.findViewById<Button>(R.id.button)
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
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
//        textView1.text = contact.name
        textView1.text = elem.getName()
        val textView2 = viewHolder.nameTextView2
//        textView2.text = contact.unit
        textView2.text = elem.getUnit()




//        val id = ValuesArray[position].this_id
//        val isOnTextChange = false


        val textView3 = viewHolder.nameTextView3
        textView3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
//                val currentPos = viewHolder.adapterPosition
                if (viewHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    ValuesArray[position].setInput(editable.toString())

                }
//                    ValuesArray.set(currentPos, editable.toString())
//                if(isOnTextChange)
                Log.d(TAG, editable.toString())

                for(elem in ValuesArray){
                    Log.d(TAG,elem.printVal())
                }
            }




        })




        val button = viewHolder.button




    }

//    override fun onBindViewHolder(viewHolder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
//        // Get the data model based on position
//        val elem: Value = ValuesArray[position]
//
//        // Set item views based on your views and data model
//        viewHolder.nameTextView1.text = elem.getName()
//        viewHolder.nameTextView2.text = elem.getUnit()
//
//        viewHolder.nameTextView3.apply {
//            setText(elem.getInput())
//            addTextChangedListener(object : TextWatcher {
//                override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
//                }
//
//                override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
//                }
//
//                override fun afterTextChanged(editable: Editable?) {
//                    if (viewHolder.adapterPosition != RecyclerView.NO_POSITION) {
//                        ValuesArray[position].setInput(editable.toString())
//                    }
//                }
//            })
//
//            setOnClickListener {
//                setText("")
//            }
//        }
//
////        viewHolder.button.setOnClickListener {
////            // Handle button click event
////        }
//    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return ValuesArray.size
    }

    fun returnValuesArray(): Array<Value> {
        return ValuesArray.toTypedArray()
    }


}