package com.example.myapp.pills_list

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.myapp.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pillName = intent.getStringExtra("pillName")

        // Twórz i wyświetl powiadomienie za pomocą NotificationManager
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Utwórz kanał powiadomień (dla Androida 8.0 i nowszych)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "pill_channel",
                "Tabletki",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Utwórz intencję dla powiadomienia
        val mainIntent = Intent(context, UserScheduleActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Utwórz powiadomienie
        val notification = NotificationCompat.Builder(context, "pill_channel")
            .setContentTitle("Zażyj tabletkę!")
            .setContentText("Czas zażycia tabletki: $pillName")
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Wyślij powiadomienie
        notificationManager.notify(1, notification)
    }
}