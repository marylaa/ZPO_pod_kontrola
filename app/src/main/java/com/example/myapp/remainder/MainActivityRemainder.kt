package com.example.myapp.remainder


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.R
import com.example.myapp.remainder.AlarmReceiver
import java.util.*

class MainActivityRemainder : AppCompatActivity(), View.OnClickListener {

    private var notificationId: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_remainder)

        val addButton = findViewById<Button>(R.id.addRemainder)
        addButton.setOnClickListener(this)

        val cancelButton = findViewById<Button>(R.id.cancelRemainder)
        cancelButton.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        val editText = findViewById<EditText>(R.id.editTextRemainder)
        val timePicker = findViewById<TimePicker>(R.id.timePicker)

        val intent = Intent(this@MainActivityRemainder, AlarmReceiver::class.java)
        intent.putExtra("id", notificationId)
        intent.putExtra("todo", editText.text.toString())

//        val alarmIntent = PendingIntent.getBroadcast(
//            this@MainActivityRemainder,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )

        val alarmIntent = PendingIntent.getBroadcast(
            applicationContext,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        val alarm: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        if (view != null) {
            when (view.id) {
                R.id.addRemainder -> {
                    val hour = timePicker.currentHour
                    val minute = timePicker.currentMinute
                    val startTime: Calendar = Calendar.getInstance()
                    startTime.set(Calendar.HOUR_OF_DAY, hour)
                    startTime.set(Calendar.MINUTE, minute)
                    startTime.set(Calendar.SECOND, 0)
                    val alarmStartTime: Long = startTime.timeInMillis

                    val interval = AlarmManager.INTERVAL_DAY

                    alarm.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        alarmStartTime,
                        interval,
                        alarmIntent
                    )
                    Toast.makeText(this, "Zrobione", Toast.LENGTH_LONG).show()
                }
                R.id.cancelRemainder -> {
                    alarm.cancel(alarmIntent)
                    Toast.makeText(this, "Anulowano", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
