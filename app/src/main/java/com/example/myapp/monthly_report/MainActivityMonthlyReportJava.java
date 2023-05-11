//package com.example.myapp.monthly_report;
//
//import android.graphics.Color;
//import android.os.Build;
//import android.os.Bundle;
//
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.myapp.R;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//
//public class MainActivityMonthlyReportJava extends AppCompatActivity{
//
////    private RecyclerView rvAnimal, rvItem;
//    private RecycleViewAdapter adapterDate;
//    private RecycleViewAdapterItem adapter;
//
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_monthly);
//
//
//        // data to populate the RecyclerView with
//        ArrayList<Integer> viewColors = new ArrayList<>();
//        viewColors.add(Color.BLUE);
//        viewColors.add(Color.YELLOW);
//        viewColors.add(Color.MAGENTA);
//
//
//        ArrayList<LocalDate> dates = new ArrayList<>();
//        LocalDate tomorrow = LocalDate.now().plusDays(1);
//        LocalDate tomorrow1 = LocalDate.now().plusDays(2);
//        LocalDate tomorrow2 = LocalDate.now().plusDays(3);
//        dates.add(tomorrow);
//        dates.add(tomorrow1);
//        dates.add(tomorrow2);
//
//        // set up the RecyclerView
//        RecyclerView recyclerView1 = findViewById(R.id.rvAnimals);
//        LinearLayoutManager horizontalLayoutManager
//                = new LinearLayoutManager(MainActivityMonthlyReportJava.this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerView1.setLayoutManager(horizontalLayoutManager);
//        adapterDate = new RecycleViewAdapter(this, viewColors, dates);
////        adapterDate.setClickListener(this);
//        recyclerView1.setAdapter(adapterDate);
//
//
//
//        // data to populate the RecyclerView with
//        ArrayList<String> date = new ArrayList<>();
//        date.add("13:00");
//        date.add("16:30");
//
//
//
//        ArrayList<String> doctors = new ArrayList<>();
//        doctors.add("lek.med. Jan Kowalski");
//        doctors.add("lek.med Anna Maria");
//
//        // set up the RecyclerView
//        RecyclerView recyclerView = findViewById(R.id.rvItems);
//        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(MainActivityMonthlyReportJava.this, LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(verticalLayoutManager);
//        adapter = new RecycleViewAdapterItem(this, date, doctors);
////        adapter.setClickListener(this);
//        recyclerView.setAdapter(adapter);
//
//
//
//
//
//
//
//    }
//
//
//
//
//}
