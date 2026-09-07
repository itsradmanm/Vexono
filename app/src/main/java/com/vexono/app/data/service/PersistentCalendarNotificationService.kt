package com.vexono.app.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.vexono.app.MainActivity
import com.vexono.app.R
import com.vexono.app.data.calendar.JalaliCalendarEngine

class PersistentCalendarNotificationService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForegroundServiceWithNotification()
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "تقویم همیشگی",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "نمایش تاریخ روز در نوار وضعیت"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun startForegroundServiceWithNotification() {
        val todayJalali = JalaliCalendarEngine.getTodayJalali()
        val dayOfWeekIndex = JalaliCalendarEngine.getDayOfWeek(todayJalali)
        val weekdayName = JalaliCalendarEngine.PERSIAN_WEEKDAY_NAMES.getOrElse(dayOfWeekIndex) { "" }
        val monthName = JalaliCalendarEngine.PERSIAN_MONTH_NAMES.getOrElse(todayJalali.month - 1) { "" }
        val dayNumberPersian = JalaliCalendarEngine.toPersianDigits(todayJalali.day)

        val title = "$weekdayName، $dayNumberPersian $monthName"
        val content = "تقویم وکسونو فعال است."

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) // TODO: use specific calendar icon based on day
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    companion object {
        const val CHANNEL_ID = "vexono_persistent_calendar"
        const val NOTIFICATION_ID = 1001
    }
}
