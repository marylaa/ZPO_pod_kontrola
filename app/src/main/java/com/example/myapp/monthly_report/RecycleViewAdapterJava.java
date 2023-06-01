package com.example.myapp.monthly_report;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.style.DrawableMarginSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecycleViewAdapterJava extends RecyclerView.Adapter<RecycleViewAdapterJava.ViewHolder> {

    private List<Integer> mViewColors;
    private List<LocalDate> mdate;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    RecycleViewAdapterJava(Context context, List<Integer> colors, List<LocalDate> date) {
        this.mInflater = LayoutInflater.from(context);
        this.mViewColors = colors;
        this.mdate = date;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycleview_item, parent, false);
        return new ViewHolder(view);
    }


    // binds the data to the view and textview in each row
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int color = mViewColors.get(position);
        LocalDate date = mdate.get(position);
        holder.myView.setColorFilter(color);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM", new Locale("pl"));
        String formattedDate = date.format(formatter);

//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM");
//        String formattedDate = date.format(formatter);
        holder.myTextView.setText(formattedDate);
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mdate.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView myView;
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myView = itemView.findViewById(R.id.colorView);
            myTextView = itemView.findViewById(R.id.tvAnimalName);
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
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
