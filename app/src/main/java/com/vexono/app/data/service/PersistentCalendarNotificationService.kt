package com.vexono.app.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.vexono.app.MainActivity
import com.vexono.app.data.calendar.JalaliCalendarEngine
import com.vexono.app.data.local.database.VexonoDatabase
import com.vexono.app.data.widget.MidnightUpdateReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * PersistentCalendarNotificationService: A foreground service that shows
 * a persistent notification in the status bar displaying today's Jalali date
 * and optionally the current day's occasion/holiday.
 *
 * This service:
 * - Starts on boot (via MidnightUpdateReceiver.BOOT_COMPLETED)
 * - Updates at midnight via MidnightUpdateReceiver
 * - Displays: date, weekday, occasion (if any), event/task count
 */
class PersistentCalendarNotificationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        Log.d(TAG, "PersistentCalendarNotificationService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand action=${intent?.action}")
        updatePersistentNotification()
        return START_STICKY // Restart if killed
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d(TAG, "PersistentCalendarNotificationService destroyed")
    }

    private fun updatePersistentNotification() {
        val todayJalali = JalaliCalendarEngine.getTodayJalali()
        val todayGregorian = JalaliCalendarEngine.jalaliToGregorian(todayJalali)
        val dayOfWeekIndex = JalaliCalendarEngine.getDayOfWeek(todayJalali)
        val weekdayName = JalaliCalendarEngine.PERSIAN_WEEKDAY_NAMES.getOrElse(dayOfWeekIndex) { "" }
        val monthName = JalaliCalendarEngine.PERSIAN_MONTH_NAMES.getOrElse(todayJalali.month - 1) { "" }
        val dayPersian = JalaliCalendarEngine.toPersianDigits(todayJalali.day)
        val yearPersian = JalaliCalendarEngine.toPersianDigits(todayJalali.year)

        val dateTitle = "$weekdayName ${dayPersian} $monthName $yearPersian"

        // Start with base notification immediately
        val baseNotification = buildNotification(
            title = dateTitle,
            text = "وکسونو - امروز ${JalaliCalendarEngine.getFullGregorianDateString(todayGregorian)}",
            isHoliday = false
        )
        startForeground(NOTIFICATION_ID_PERSISTENT, baseNotification)

        // Fetch richer data asynchronously
        serviceScope.launch {
            try {
                val db = VexonoDatabase.getDatabase(applicationContext)

                val occasions = db.occasionDao().getOccasionsForDay(
                    todayJalali.year, todayJalali.month, todayJalali.day
                ).firstOrNull() ?: emptyList()

                val events = db.eventDao().getEventsForDay(
                    todayJalali.year, todayJalali.month, todayJalali.day
                ).firstOrNull() ?: emptyList()

                val tasks = db.taskDao().getTasksForDay(
                    todayJalali.year, todayJalali.month, todayJalali.day
                ).firstOrNull() ?: emptyList()

                val isHoliday = occasions.any { it.isHoliday }

                val summaryParts = mutableListOf<String>()
                if (occasions.isNotEmpty()) {
                    summaryParts.add(occasions.first().title)
                }
                if (events.isNotEmpty()) {
                    summaryParts.add("📅 ${JalaliCalendarEngine.toPersianDigits(events.size)} رویداد")
                }
                if (tasks.isNotEmpty()) {
                    val completed = tasks.count { it.isCompleted }
                    summaryParts.add("✅ ${JalaliCalendarEngine.toPersianDigits(completed)}/${JalaliCalendarEngine.toPersianDigits(tasks.size)} تسک")
                }

                val summaryText = if (summaryParts.isNotEmpty()) {
                    summaryParts.joinToString("  |  ")
                } else {
                    "وکسونو - ${JalaliCalendarEngine.getFullGregorianDateString(todayGregorian)}"
                }

                val richNotification = buildNotification(
                    title = if (isHoliday) "🔴 $dateTitle (تعطیل رسمی)" else dateTitle,
                    text = summaryText,
                    isHoliday = isHoliday
                )

                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTIFICATION_ID_PERSISTENT, richNotification)

            } catch (e: Exception) {
                Log.e(TAG, "Error updating persistent notification", e)
            }
        }
    }

    private fun buildNotification(title: String, text: String, isHoliday: Boolean): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        return NotificationCompat.Builder(this, CHANNEL_ID_PERSISTENT)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // Cannot be dismissed by swiping
            .setSilent(true) // No sound/vibration
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setColor(if (isHoliday) 0xFFFF5C7A.toInt() else 0xFF7C4DFF.toInt())
            .build()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_PERSISTENT,
                CHANNEL_NAME_PERSISTENT,
                NotificationManager.IMPORTANCE_LOW // Low importance = no sound, shows in status bar
            ).apply {
                description = CHANNEL_DESC_PERSISTENT
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val TAG = "PersistentCalService"
        const val NOTIFICATION_ID_PERSISTENT = 1001
        const val CHANNEL_ID_PERSISTENT = "vexono_persistent_calendar"
        const val CHANNEL_NAME_PERSISTENT = "تقویم روز وکسونو (نوار وضعیت)"
        const val CHANNEL_DESC_PERSISTENT = "نمایش تاریخ شمسی امروز به صورت دائم در نوار وضعیت"

        fun start(context: Context) {
            val intent = Intent(context, PersistentCalendarNotificationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, PersistentCalendarNotificationService::class.java)
            context.stopService(intent)
        }
    }
}
