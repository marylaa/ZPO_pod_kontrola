package com.example.myapp.monthly_report;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.report.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;

public class MainActivityMonthlyReport extends AppCompatActivity{

//    private RecyclerView rvAnimal, rvItem;
    private RecycleViewAdapter adapterDate;
    private RecycleViewAdapterItem adapter;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_monthly);


        // data to populate the RecyclerView with
        ArrayList<Integer> viewColors = new ArrayList<>();
        viewColors.add(Color.BLUE);
        viewColors.add(Color.YELLOW);
        viewColors.add(Color.MAGENTA);


        ArrayList<LocalDate> dates = new ArrayList<>();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate tomorrow1 = LocalDate.now().plusDays(2);
        LocalDate tomorrow2 = LocalDate.now().plusDays(3);
        dates.add(tomorrow);
        dates.add(tomorrow1);
        dates.add(tomorrow2);

        // set up the RecyclerView
        RecyclerView recyclerView1 = findViewById(R.id.rvAnimals);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(MainActivityMonthlyReport.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView1.setLayoutManager(horizontalLayoutManager);
        adapterDate = new RecycleViewAdapter(this, viewColors, dates);
//        adapterDate.setClickListener(this);
        recyclerView1.setAdapter(adapterDate);



        // data to populate the RecyclerView with
        ArrayList<String> date = new ArrayList<>();
        date.add("13:00");
        date.add("16:30");



        ArrayList<String> doctors = new ArrayList<>();
        doctors.add("lek.med. Jan Kowalski");
        doctors.add("lek.med Anna Maria");

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvItems);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(MainActivityMonthlyReport.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalLayoutManager);
        adapter = new RecycleViewAdapterItem(this, date, doctors);
//        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


//        var lineChart = findViewById<LineChart>(R.id.line_chart);
//
//        setupLineChart();




    }

//    public fun setupLineChart() {
//        // Get reference to the Firebase database
////        val database = FirebaseDatabase.getInstance();
////        val reference = database.reference.child("data");
//
//        // Initialize an empty list of entries
//        val entries = mutableListOf<Entry>();
//
//        // Set up a listener to retrieve the data from the database
//        reference.addValueEventListener(object :ValueEventListener {
//            override fun onDataChange(snapshot:DataSnapshot) {
//                // Iterate through the children of the "data" node
//                for (child in snapshot.children) {
//                    // Get the x and y values from the child
//                    val x = child.key?.toFloat() ?: 0f
//                    val y = child.value?.toString()?.toFloat() ?: 0f
//
//                    // Add a new entry to the list
//                    entries.add(Entry(x, y))
//                }
//
//                // Create a new data set and add the entries to it
//                val dataSet = LineDataSet(entries, "Data Set")
//                dataSet.color = Color.BLUE
//                dataSet.valueTextColor = Color.BLACK
//
//                // Create a new line data object and set the data set
//                val lineData = LineData(dataSet)
//
//                // Set the data to the line chart
//                lineChart.data = lineData
//
//                // Refresh the chart
//                lineChart.invalidate()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle errors here
//            }
//        })
//    }

//    @Override
//    public void onItemClick(View view, int position) {
//        Toast.makeText(this, "You clicked " + adapterDate.getItem(position) + " on item position " + position, Toast.LENGTH_SHORT).show();
//    }

//    @Override
//    public void onItemClick(View view, int position) {
//        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on item position " + position, Toast.LENGTH_SHORT).show();
//    }


}
