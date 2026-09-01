package com.vexono.app.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vexono.app.MainActivity

object NotificationHelper {

    const val CHANNEL_ID_REMINDERS = "vexono_reminders"
    const val CHANNEL_NAME_REMINDERS = "یادآورهای تقویم Vexono"
    const val CHANNEL_DESC_REMINDERS = "اعلان رویدادها و یادآورهای تقویم شمسی و میلادی"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_REMINDERS,
                CHANNEL_NAME_REMINDERS,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC_REMINDERS
                enableVibration(true)
                enableLights(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showEventNotification(
        context: Context,
        notificationId: Int,
        title: String,
        description: String,
        timeString: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("⏰ $title")
            .setContentText(description.ifBlank { "زمان رویداد: $timeString" })
            .setStyle(NotificationCompat.BigTextStyle().bigText(if (description.isNotBlank()) "$description\nزمان: $timeString" else "زمان رویداد: $timeString"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        try {
            notificationManager.notify(notificationId, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
