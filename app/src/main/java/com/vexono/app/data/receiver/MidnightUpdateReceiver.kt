package com.vexono.app.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.vexono.app.data.service.PersistentCalendarNotificationService
import com.vexono.app.data.widget.VexonoWidgetProvider

class MidnightUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_DATE_CHANGED || action == Intent.ACTION_TIMEZONE_CHANGED || action == "com.vexono.app.MIDNIGHT_UPDATE") {
            Log.d("MidnightUpdateReceiver", "Midnight/Date changed. Updating widgets and notification.")
            
            // 1. Update Widgets
            VexonoWidgetProvider.sendUpdateBroadcast(context)
            
            // 2. Update Notification Service
            val serviceIntent = Intent(context, PersistentCalendarNotificationService::class.java)
            serviceIntent.action = "UPDATE_NOTIFICATION"
            try {
                context.startService(serviceIntent)
            } catch (e: IllegalStateException) {
                // If app is in background in Android 8+, startService will throw.
                // Depending on settings, we may want to use ContextCompat.startForegroundService
            }
        }
    }
}
