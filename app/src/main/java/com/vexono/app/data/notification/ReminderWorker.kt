package com.vexono.app.data.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.vexono.app.data.calendar.JalaliCalendarEngine
import com.vexono.app.domain.model.Event
import com.vexono.app.domain.model.JalaliDate
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: "رویداد تقویم"
        val description = inputData.getString("description") ?: ""
        val eventId = inputData.getLong("event_id", 0L)
        val timeString = inputData.getString("time_string") ?: ""

        NotificationHelper.createNotificationChannels(applicationContext)
        NotificationHelper.showEventNotification(
            context = applicationContext,
            notificationId = eventId.toInt(),
            title = title,
            description = description,
            timeString = timeString
        )

        return Result.success()
    }
}

object AlarmScheduler {

    fun scheduleEventReminder(context: Context, event: Event) {
        if (!event.hasReminder) return

        // Convert Jalali event date to Gregorian Calendar timestamp
        val jDate = JalaliDate(event.jalaliYear, event.jalaliMonth, event.jalaliDay)
        val gDate = JalaliCalendarEngine.jalaliToGregorian(jDate)

        val targetCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, gDate.year)
            set(Calendar.MONTH, gDate.month - 1)
            set(Calendar.DAY_OF_MONTH, gDate.day)
            set(Calendar.HOUR_OF_DAY, event.hour)
            set(Calendar.MINUTE, event.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MINUTE, -event.reminderMinutesBefore)
        }

        val delayMillis = targetCal.timeInMillis - System.currentTimeMillis()
        if (delayMillis <= 0) return // Skip if in the past

        val timeFormatted = String.format("%02d:%02d", event.hour, event.minute)
        val inputData = Data.Builder()
            .putLong("event_id", event.id)
            .putString("title", event.title)
            .putString("description", event.description)
            .putString("time_string", JalaliCalendarEngine.toPersianDigits(timeFormatted))
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "event_reminder_${event.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelEventReminder(context: Context, eventId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("event_reminder_$eventId")
    }
}
