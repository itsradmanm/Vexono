package com.vexono.app.data.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.vexono.app.MainActivity
import com.vexono.app.R
import com.vexono.app.data.calendar.JalaliCalendarEngine
import com.vexono.app.data.local.database.VexonoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * Samsung One UI style widget providers for Vexono:
 *   - VexonoWidgetProvider:       2×2 Month Grid (Image 1)
 *   - VexonoBarWidgetProvider:    2×1 Today Pill (Image 2)
 *   - VexonoSquareWidgetProvider: 2×2 Today Minimal (Image 2)
 *   - VexonoAgendaWidgetProvider: 4×2 Month Grid + Agenda (Image 2)
 *   - VexonoEventsWidgetProvider: 4×2 Upcoming Events List (Image 3)
 */
class VexonoWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        MidnightUpdateReceiver.schedule(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        MidnightUpdateReceiver.cancel(context)
    }

    companion object {
        private const val TAG = "VexonoWidget"

        // ==========================================
        // 1. Month Grid Widget (2×2) - Image 1
        // ==========================================
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val todayJalali = JalaliCalendarEngine.getTodayJalali()
            val monthName = JalaliCalendarEngine.PERSIAN_MONTH_NAMES.getOrElse(todayJalali.month - 1) { "" }

            val views = RemoteViews(context.packageName, R.layout.widget_vexono_today)
            views.setTextViewText(R.id.widget_month_name, "$monthName ${JalaliCalendarEngine.toPersianDigits(todayJalali.year)}")
            fillMonthGridCells(context, views, todayJalali, "m")
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent(context, 0))

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        // ==========================================
        // 2. Bar Pill Widget (2×1) - Image 2
        // ==========================================
        fun updateBarWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val todayJalali = JalaliCalendarEngine.getTodayJalali()
            val dayOfWeekIndex = JalaliCalendarEngine.getDayOfWeek(todayJalali)
            val weekdayName = JalaliCalendarEngine.PERSIAN_WEEKDAY_NAMES.getOrElse(dayOfWeekIndex) { "" }
            val monthName = JalaliCalendarEngine.PERSIAN_MONTH_NAMES.getOrElse(todayJalali.month - 1) { "" }
            val dayNumberPersian = JalaliCalendarEngine.toPersianDigits(todayJalali.day)

            val views = RemoteViews(context.packageName, R.layout.widget_vexono_bar)
            views.setTextViewText(R.id.bar_day_number, dayNumberPersian)
            views.setTextViewText(R.id.bar_month_name, monthName)
            views.setTextViewText(R.id.bar_weekday, weekdayName)
            views.setOnClickPendingIntent(R.id.widget_bar_root, pendingIntent(context, 1))

            // Fetch occasion in background
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = VexonoDatabase.getDatabase(context)
                    val occasions = db.occasionDao().getOccasionsForDay(
                        todayJalali.year, todayJalali.month, todayJalali.day
                    ).firstOrNull()

                    if (!occasions.isNullOrEmpty()) {
                        views.setViewVisibility(R.id.bar_occasion, View.VISIBLE)
                        views.setTextViewText(R.id.bar_occasion, occasions.first().title)
                    } else {
                        views.setViewVisibility(R.id.bar_occasion, View.GONE)
                    }
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching occasion for bar widget", e)
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        // ==========================================
        // 3. Square Minimal Widget (2×2) - Image 2
        // ==========================================
        fun updateSquareWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val todayJalali = JalaliCalendarEngine.getTodayJalali()
            val dayOfWeekIndex = JalaliCalendarEngine.getDayOfWeek(todayJalali)
            val weekdayName = JalaliCalendarEngine.PERSIAN_WEEKDAY_NAMES.getOrElse(dayOfWeekIndex) { "" }
            val monthName = JalaliCalendarEngine.PERSIAN_MONTH_NAMES.getOrElse(todayJalali.month - 1) { "" }
            val dayNumberPersian = JalaliCalendarEngine.toPersianDigits(todayJalali.day)
            val yearPersian = JalaliCalendarEngine.toPersianDigits(todayJalali.year)

            val views = RemoteViews(context.packageName, R.layout.widget_vexono_square)
            views.setTextViewText(R.id.square_month_year, "$monthName $yearPersian")
            views.setTextViewText(R.id.square_day_number, dayNumberPersian)
            views.setTextViewText(R.id.square_weekday, weekdayName)
            views.setOnClickPendingIntent(R.id.widget_square_root, pendingIntent(context, 2))

            // Fetch occasion in background
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = VexonoDatabase.getDatabase(context)
                    val occasions = db.occasionDao().getOccasionsForDay(
                        todayJalali.year, todayJalali.month, todayJalali.day
                    ).firstOrNull()

                    if (!occasions.isNullOrEmpty()) {
                        views.setViewVisibility(R.id.square_occasion, View.VISIBLE)
                        views.setTextViewText(R.id.square_occasion, occasions.first().title)
                    } else {
                        views.setViewVisibility(R.id.square_occasion, View.GONE)
                    }
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching occasion for square widget", e)
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        // ==========================================
        // 4. Agenda Widget (4×2 Month + Events) - Image 2
        // ==========================================
        fun updateAgendaWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val todayJalali = JalaliCalendarEngine.getTodayJalali()
            val monthName = JalaliCalendarEngine.PERSIAN_MONTH_NAMES.getOrElse(todayJalali.month - 1) { "" }
            val dayNumberPersian = JalaliCalendarEngine.toPersianDigits(todayJalali.day)

            val views = RemoteViews(context.packageName, R.layout.widget_vexono_agenda)

            // Fill left calendar grid
            fillMonthGridCells(context, views, todayJalali, "ag")

            // Right header
            views.setTextViewText(R.id.ag_header_date, "$dayNumberPersian $monthName")
            views.setOnClickPendingIntent(R.id.widget_agenda_root, pendingIntent(context, 3))
            views.setOnClickPendingIntent(R.id.ag_btn_add, pendingIntent(context, 3))

            // Fetch events in background
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = VexonoDatabase.getDatabase(context)
                    val events = db.eventDao().getEventsForDay(
                        todayJalali.year, todayJalali.month, todayJalali.day
                    ).firstOrNull() ?: emptyList()

                    val tasks = db.taskDao().getAllTasks().firstOrNull()
                        ?.filter { !it.isCompleted }
                        ?: emptyList()

                    if (events.isEmpty() && tasks.isEmpty()) {
                        views.setViewVisibility(R.id.ag_empty_layout, View.VISIBLE)
                        views.setViewVisibility(R.id.ag_events_layout, View.GONE)
                    } else {
                        views.setViewVisibility(R.id.ag_empty_layout, View.GONE)
                        views.setViewVisibility(R.id.ag_events_layout, View.VISIBLE)

                        if (events.isNotEmpty()) {
                            val ev1 = events[0]
                            views.setViewVisibility(R.id.ag_item1, View.VISIBLE)
                            views.setTextViewText(R.id.ag_item1_title, ev1.title)
                            val timeStr = "${JalaliCalendarEngine.toPersianDigits(String.format("%02d", ev1.hour))}:${JalaliCalendarEngine.toPersianDigits(String.format("%02d", ev1.minute))}"
                            views.setTextViewText(R.id.ag_item1_time, timeStr)
                        } else if (tasks.isNotEmpty()) {
                            val t1 = tasks[0]
                            views.setViewVisibility(R.id.ag_item1, View.VISIBLE)
                            views.setTextViewText(R.id.ag_item1_title, t1.title)
                            views.setTextViewText(R.id.ag_item1_time, "وظیفه روز")
                        } else {
                            views.setViewVisibility(R.id.ag_item1, View.GONE)
                        }

                        if (events.size > 1) {
                            val ev2 = events[1]
                            views.setViewVisibility(R.id.ag_item2, View.VISIBLE)
                            views.setTextViewText(R.id.ag_item2_title, ev2.title)
                            val timeStr = "${JalaliCalendarEngine.toPersianDigits(String.format("%02d", ev2.hour))}:${JalaliCalendarEngine.toPersianDigits(String.format("%02d", ev2.minute))}"
                            views.setTextViewText(R.id.ag_item2_time, timeStr)
                        } else if (events.size == 1 && tasks.isNotEmpty()) {
                            val t1 = tasks[0]
                            views.setViewVisibility(R.id.ag_item2, View.VISIBLE)
                            views.setTextViewText(R.id.ag_item2_title, t1.title)
                            views.setTextViewText(R.id.ag_item2_time, "وظیفه")
                        } else {
                            views.setViewVisibility(R.id.ag_item2, View.GONE)
                        }
                    }

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching agenda data", e)
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        // ==========================================
        // 5. Events Widget (4×2 List) - Image 3
        // ==========================================
        fun updateEventsWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val todayJalali = JalaliCalendarEngine.getTodayJalali()
            val dayOfWeekIndex = JalaliCalendarEngine.getDayOfWeek(todayJalali)
            val weekdayName = JalaliCalendarEngine.PERSIAN_WEEKDAY_NAMES.getOrElse(dayOfWeekIndex) { "" }
            val monthName = JalaliCalendarEngine.PERSIAN_MONTH_NAMES.getOrElse(todayJalali.month - 1) { "" }
            val dayNumberPersian = JalaliCalendarEngine.toPersianDigits(todayJalali.day)

            val views = RemoteViews(context.packageName, R.layout.widget_vexono_events)
            views.setTextViewText(
                R.id.events_header_title,
                "امروز، $weekdayName $dayNumberPersian $monthName"
            )
            views.setOnClickPendingIntent(R.id.widget_events_root, pendingIntent(context, 4))
            views.setOnClickPendingIntent(R.id.events_btn_add, pendingIntent(context, 4))

            // Fetch events and occasions in background
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = VexonoDatabase.getDatabase(context)
                    val occasions = db.occasionDao().getOccasionsForDay(
                        todayJalali.year, todayJalali.month, todayJalali.day
                    ).firstOrNull() ?: emptyList()

                    val events = db.eventDao().getEventsForDay(
                        todayJalali.year, todayJalali.month, todayJalali.day
                    ).firstOrNull() ?: emptyList()

                    val tasks = db.taskDao().getAllTasks().firstOrNull()
                        ?.filter { !it.isCompleted }
                        ?: emptyList()

                    // Combine into items
                    data class DisplayItem(val title: String, val time: String, val icon: String)
                    val items = mutableListOf<DisplayItem>()

                    for (occ in occasions.take(1)) {
                        items.add(DisplayItem(occ.title, "مناسبت امروز", "🎉"))
                    }
                    for (ev in events) {
                        val timeStr = "${JalaliCalendarEngine.toPersianDigits(String.format("%02d", ev.hour))}:${JalaliCalendarEngine.toPersianDigits(String.format("%02d", ev.minute))}"
                        items.add(DisplayItem(ev.title, timeStr, "📅"))
                    }
                    for (task in tasks) {
                        items.add(DisplayItem(task.title, "وظیفه", "✅"))
                    }

                    if (items.isEmpty()) {
                        views.setViewVisibility(R.id.events_empty_text, View.VISIBLE)
                        views.setViewVisibility(R.id.events_list_container, View.GONE)
                    } else {
                        views.setViewVisibility(R.id.events_empty_text, View.GONE)
                        views.setViewVisibility(R.id.events_list_container, View.VISIBLE)

                        // Item 1
                        if (items.isNotEmpty()) {
                            views.setViewVisibility(R.id.ev_row_1, View.VISIBLE)
                            views.setTextViewText(R.id.ev_title_1, items[0].title)
                            views.setTextViewText(R.id.ev_time_1, items[0].time)
                            views.setTextViewText(R.id.ev_icon_1, items[0].icon)
                        } else {
                            views.setViewVisibility(R.id.ev_row_1, View.GONE)
                        }

                        // Item 2
                        if (items.size > 1) {
                            views.setViewVisibility(R.id.ev_div_1, View.VISIBLE)
                            views.setViewVisibility(R.id.ev_row_2, View.VISIBLE)
                            views.setTextViewText(R.id.ev_title_2, items[1].title)
                            views.setTextViewText(R.id.ev_time_2, items[1].time)
                            views.setTextViewText(R.id.ev_icon_2, items[1].icon)
                        } else {
                            views.setViewVisibility(R.id.ev_div_1, View.GONE)
                            views.setViewVisibility(R.id.ev_row_2, View.GONE)
                        }

                        // Item 3
                        if (items.size > 2) {
                            views.setViewVisibility(R.id.ev_div_2, View.VISIBLE)
                            views.setViewVisibility(R.id.ev_row_3, View.VISIBLE)
                            views.setTextViewText(R.id.ev_title_3, items[2].title)
                            views.setTextViewText(R.id.ev_time_3, items[2].time)
                            views.setTextViewText(R.id.ev_icon_3, items[2].icon)
                        } else {
                            views.setViewVisibility(R.id.ev_div_2, View.GONE)
                            views.setViewVisibility(R.id.ev_row_3, View.GONE)
                        }
                    }

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching events widget data", e)
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        // Helper: Fills a 6-row x 7-column month grid in RemoteViews
        private fun fillMonthGridCells(
            context: Context,
            views: RemoteViews,
            todayJalali: com.vexono.app.domain.model.JalaliDate,
            prefix: String
        ) {
            val firstDayOfMonth = com.vexono.app.domain.model.JalaliDate(todayJalali.year, todayJalali.month, 1)
            val startDayOfWeek = JalaliCalendarEngine.getDayOfWeek(firstDayOfMonth) // 0=Sat..6=Fri
            val daysInMonth = JalaliCalendarEngine.getDaysInJalaliMonth(todayJalali.year, todayJalali.month)

            for (i in 0 until 42) {
                val r = i / 7
                val c = i % 7
                val txtResName = "${prefix}_txt_${r}_${c}"
                val bgResName = "${prefix}_bg_${r}_${c}"
                val txtResId = context.resources.getIdentifier(txtResName, "id", context.packageName)
                val bgResId = context.resources.getIdentifier(bgResName, "id", context.packageName)
                if (txtResId == 0) continue

                val dayNumber = i - startDayOfWeek + 1
                if (dayNumber in 1..daysInMonth) {
                    views.setTextViewText(txtResId, JalaliCalendarEngine.toPersianDigits(dayNumber))
                    if (dayNumber == todayJalali.day) {
                        if (bgResId != 0) views.setViewVisibility(bgResId, View.VISIBLE)
                        views.setTextColor(txtResId, Color.BLACK)
                    } else {
                        if (bgResId != 0) views.setViewVisibility(bgResId, View.GONE)
                        val color = if (c == 6) Color.parseColor("#FF5252") else Color.parseColor("#E0E0E6")
                        views.setTextColor(txtResId, color)
                    }
                } else {
                    views.setTextViewText(txtResId, "")
                    if (bgResId != 0) views.setViewVisibility(bgResId, View.GONE)
                }
            }
        }

        fun sendUpdateBroadcast(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)

            // Update month widget
            val todayIds = appWidgetManager.getAppWidgetIds(ComponentName(context, VexonoWidgetProvider::class.java))
            for (id in todayIds) updateAppWidget(context, appWidgetManager, id)

            // Update bar widget
            val barIds = appWidgetManager.getAppWidgetIds(ComponentName(context, VexonoBarWidgetProvider::class.java))
            for (id in barIds) updateBarWidget(context, appWidgetManager, id)

            // Update square widget
            val squareIds = appWidgetManager.getAppWidgetIds(ComponentName(context, VexonoSquareWidgetProvider::class.java))
            for (id in squareIds) updateSquareWidget(context, appWidgetManager, id)

            // Update agenda widget
            val agendaIds = appWidgetManager.getAppWidgetIds(ComponentName(context, VexonoAgendaWidgetProvider::class.java))
            for (id in agendaIds) updateAgendaWidget(context, appWidgetManager, id)

            // Update events widget
            val eventIds = appWidgetManager.getAppWidgetIds(ComponentName(context, VexonoEventsWidgetProvider::class.java))
            for (id in eventIds) updateEventsWidget(context, appWidgetManager, id)
        }

        private fun pendingIntent(context: Context, requestCode: Int): PendingIntent {
            val intent = Intent(context, MainActivity::class.java)
            return PendingIntent.getActivity(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            )
        }
    }
}

/** Bar (2×1) Widget Provider */
class VexonoBarWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) VexonoWidgetProvider.updateBarWidget(context, appWidgetManager, id)
    }
    override fun onEnabled(context: Context) { MidnightUpdateReceiver.schedule(context) }
    override fun onDisabled(context: Context) {}
}

/** Square (2×2) Widget Provider */
class VexonoSquareWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) VexonoWidgetProvider.updateSquareWidget(context, appWidgetManager, id)
    }
    override fun onEnabled(context: Context) { MidnightUpdateReceiver.schedule(context) }
    override fun onDisabled(context: Context) {}
}

/** Agenda (4×2) Widget Provider */
class VexonoAgendaWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) VexonoWidgetProvider.updateAgendaWidget(context, appWidgetManager, id)
    }
    override fun onEnabled(context: Context) { MidnightUpdateReceiver.schedule(context) }
    override fun onDisabled(context: Context) {}
}

/** Upcoming Events (4×2) Widget Provider */
class VexonoEventsWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) VexonoWidgetProvider.updateEventsWidget(context, appWidgetManager, id)
    }
    override fun onEnabled(context: Context) { MidnightUpdateReceiver.schedule(context) }
    override fun onDisabled(context: Context) {}
}
