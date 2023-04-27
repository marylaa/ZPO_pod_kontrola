package com.example.myapp.monthly_report;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;

import java.util.List;

public class RecycleViewAdapterItemJava extends RecyclerView.Adapter<RecycleViewAdapterItemJava.ViewHolder> {

    private List<String> mdate;
    private List<String> mDoctors;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    RecycleViewAdapterItemJava(Context context, List<String> mdate, List<String> mDoctors) {
        this.mInflater = LayoutInflater.from(context);
        this.mdate = mdate;
        this.mDoctors = mDoctors;
    }

//    // inflates the row layout from xml when needed
//    @Override
//    @NonNull
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = mInflater.inflate(R.layout.contact, parent, false);
//        return new ViewHolder(view);
//    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View contactView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_monthly, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String date = mdate.get(position);
        String doctor = mDoctors.get(position);
        holder.myTextView.setText(date);
        holder.myTextView1.setText(doctor);

    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mdate.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View myView;
        TextView myTextView;
        TextView myTextView1;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.contact_name1);
            myTextView1 = itemView.findViewById(R.id.contact_name2);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return String.valueOf(mdate.get(id));
    }

    // allows clicks events to be caught
    public void setClickListener(RecycleViewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = (ItemClickListener) itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
