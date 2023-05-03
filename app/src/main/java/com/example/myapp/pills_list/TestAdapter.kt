package com.example.myapp.pills_list


import com.example.myapp.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView


// This is your reyclerview adapter
class TestAdapter : RecyclerView.Adapter<TestAdapter.ViewHolder>() {
    // Just storing strings for example
    private lateinit var items: MutableList<PillModel>

    // A callback that gets invoked when an item is checked (or unchecked)
    // A callback that gets invoked when an item is checked (or unchecked)
    private var callback: Callback? = null

    // Call this to add strings to the adapter
    fun addItem(item: MutableList<PillModel>) {
        this.items = item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(com.example.myapp.R.layout.item_pill, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position].toString())
    }

    // Sets the callback
    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    // Callback interface, used to notify when an item's checked status changed
    interface Callback {
        fun onCheckedChanged(item: String?, isChecked: Boolean)
    }

    // Our view holder
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox = itemView.findViewById<CheckBox>(R.id.cbDone)
        private var callback: Callback? = null

        fun bind(s: String?) {
            checkBox.text = s
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                callback?.onCheckedChanged(s, isChecked)
            }
        }

        fun setCallback(callback: Callback) {
            this.callback = callback
        }

        interface Callback {
            fun onCheckedChanged(item: String?, isChecked: Boolean)
        }
    }
}

