package com.example.myapp.remainder

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.getSystemService

class AlarmReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationId = intent?.getIntExtra("id", 0)
        val message = intent?.getStringExtra("todo")

        val mainIntent = Intent(context, MainActivityRemainder::class.java)
        val contentIntent = PendingIntent.getActivity(context, 0, mainIntent, 0)

        val builder = Notification.Builder(context)
        builder.setSmallIcon(android.R.drawable.btn_star)
            .setContentTitle("Czas na tabletkę")
            .setContentText(message)
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)
            .setContentIntent(contentIntent)

        val myNotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        myNotificationManager.notify(notificationId!!, builder.build())


    }



}