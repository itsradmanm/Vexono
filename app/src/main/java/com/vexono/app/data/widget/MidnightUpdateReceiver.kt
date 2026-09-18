package com.vexono.app.data.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar

/**
 * MidnightUpdateReceiver listens for:
 *   - ACTION_DATE_CHANGED: system day has changed (midnight)
 *   - ACTION_TIMEZONE_CHANGED: timezone changed
 *   - BOOT_COMPLETED: device rebooted, reschedule alarms
 *   - Custom ACTION_VEXONO_MIDNIGHT_UPDATE: our exact midnight alarm
 *
 * On any of these, all Vexono widgets are updated with the new date.
 */
class MidnightUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(TAG, "Received: $action")

        when (action) {
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_BOOT_COMPLETED,
            ACTION_VEXONO_MIDNIGHT_UPDATE -> {
                // Update all widgets
                updateAllWidgets(context)
                // Reschedule the next midnight alarm
                schedule(context)
            }
        }
    }

    companion object {
        private const val TAG = "MidnightUpdateReceiver"
        const val ACTION_VEXONO_MIDNIGHT_UPDATE = "com.vexono.app.ACTION_MIDNIGHT_UPDATE"
        private const val REQUEST_CODE_MIDNIGHT = 9900

        /**
         * Schedules an exact alarm at midnight (00:00) of tomorrow.
         * Uses setExactAndAllowWhileIdle for battery efficiency.
         */
        fun schedule(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = buildPendingIntent(context)

            // Calculate midnight of tomorrow
            val calendar = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 1)
                set(Calendar.MILLISECOND, 0)
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
                Log.d(TAG, "Midnight alarm scheduled for: ${calendar.time}")
            } catch (e: SecurityException) {
                Log.w(TAG, "Cannot schedule exact alarm - falling back to inexact", e)
                // Fallback to setWindow for Android 12+ when SCHEDULE_EXACT_ALARM not granted
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    alarmManager.setWindow(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        60_000L, // 1 minute window
                        pendingIntent
                    )
                }
            }
        }

        fun cancel(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(buildPendingIntent(context))
            Log.d(TAG, "Midnight alarm cancelled")
        }

        private fun buildPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, MidnightUpdateReceiver::class.java).apply {
                action = ACTION_VEXONO_MIDNIGHT_UPDATE
            }
            return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_MIDNIGHT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                    (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            )
        }

        private fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)

            // Update original today widget
            val todayIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, VexonoWidgetProvider::class.java)
            )
            for (id in todayIds) {
                VexonoWidgetProvider.updateAppWidget(context, appWidgetManager, id)
            }

            // Update bar widget
            val barIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, VexonoBarWidgetProvider::class.java)
            )
            for (id in barIds) {
                VexonoWidgetProvider.updateBarWidget(context, appWidgetManager, id)
            }

            // Update square widget
            val squareIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, VexonoSquareWidgetProvider::class.java)
            )
            for (id in squareIds) {
                VexonoWidgetProvider.updateSquareWidget(context, appWidgetManager, id)
            }

            // Update agenda widget
            val agendaIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, VexonoAgendaWidgetProvider::class.java)
            )
            for (id in agendaIds) {
                VexonoWidgetProvider.updateAgendaWidget(context, appWidgetManager, id)
            }

            Log.d(TAG, "All widgets updated (today=${todayIds.size}, bar=${barIds.size}, square=${squareIds.size}, agenda=${agendaIds.size})")
        }
    }
}
