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
 * Main widget provider for all Vexono home screen widgets.
 * Supports three widget types identified by their layout:
 *   - Original (2×2): widget_vexono_today
 *   - Bar (2×1):      widget_vexono_bar
 *   - Square (2×2):   widget_vexono_square
 *   - Agenda (4×2):   widget_vexono_agenda
 *
 * All widgets update on every APPWIDGET_UPDATE broadcast, as well as on
 * ACTION_DATE_CHANGED and TIMEZONE_CHANGED (via MidnightUpdateReceiver).
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
        // Schedule the midnight update receiver when first widget is added
        MidnightUpdateReceiver.schedule(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        // Cancel the midnight update receiver when last widget is removed
        MidnightUpdateReceiver.cancel(context)
    }

    companion object {
        private const val TAG = "VexonoWidget"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val todayJalali = JalaliCalendarEngine.getTodayJalali()
            val todayGregorian = JalaliCalendarEngine.jalaliToGregorian(todayJalali)
            val dayOfWeekIndex = JalaliCalendarEngine.getDayOfWeek(todayJalali)

            val weekdayName = JalaliCalendarEngine.PERSIAN_WEEKDAY_NAMES.getOrElse(dayOfWeekIndex) { "" }
            val monthName = JalaliCalendarEngine.PERSIAN_MONTH_NAMES.getOrElse(todayJalali.month - 1) { "" }
            val dayNumberPersian = JalaliCalendarEngine.toPersianDigits(todayJalali.day)
            val yearPersian = JalaliCalendarEngine.toPersianDigits(todayJalali.year)
            val gregorianFormatted = JalaliCalendarEngine.getFullGregorianDateString(todayGregorian)

            // Get the widget info to determine which layout it's using
            // We try all layouts and catch exceptions gracefully
            // This approach creates RemoteViews for the originally-registered layout
            val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
            val maxWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 140)

            // Determine widget layout based on options width (heuristic)
            // The actual layout is determined when the widget is placed, so we handle all known types
            tryUpdateWithLayout(context, appWidgetManager, appWidgetId, R.layout.widget_vexono_today) { views ->
                fillTodayWidget(views, weekdayName, yearPersian, dayNumberPersian, monthName, gregorianFormatted)
                pendingIntent(context, 0).also { views.setOnClickPendingIntent(R.id.widget_root, it) }
                fetchAndFillOccasionToday(context, todayJalali, appWidgetManager, appWidgetId, views)
            }
        }

        private fun tryUpdateWithLayout(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            layoutResId: Int,
            fill: (RemoteViews) -> Unit
        ) {
            try {
                val views = RemoteViews(context.packageName, layoutResId)
                fill(views)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating widget $appWidgetId with layout $layoutResId", e)
            }
        }

        // -- Fill functions for each widget type --

        private fun fillTodayWidget(
            views: RemoteViews,
            weekdayName: String,
            yearPersian: String,
            dayNumberPersian: String,
            monthName: String,
            gregorianFormatted: String
        ) {
            views.setTextViewText(R.id.widget_weekday, weekdayName)
            views.setTextViewText(R.id.widget_year, yearPersian)
            views.setTextViewText(R.id.widget_day_number, dayNumberPersian)
            views.setTextViewText(R.id.widget_month_name, monthName)
            views.setTextViewText(R.id.widget_gregorian_date, gregorianFormatted)
        }

        private fun fillBarWidget(
            views: RemoteViews,
            weekdayName: String,
            dayNumberPersian: String,
            monthName: String
        ) {
            views.setTextViewText(R.id.bar_day_number, dayNumberPersian)
            views.setTextViewText(R.id.bar_month_name, monthName)
            views.setTextViewText(R.id.bar_weekday, weekdayName)
        }

        private fun fillSquareWidget(
            views: RemoteViews,
            weekdayName: String,
            yearPersian: String,
            dayNumberPersian: String,
            monthName: String,
            gregorianFormatted: String
        ) {
            views.setTextViewText(R.id.square_weekday, weekdayName)
            views.setTextViewText(R.id.square_year, yearPersian)
            views.setTextViewText(R.id.square_day_number, dayNumberPersian)
            views.setTextViewText(R.id.square_month_name, monthName)
            views.setTextViewText(R.id.square_gregorian_date, gregorianFormatted)
        }

        private fun fillAgendaWidget(
            views: RemoteViews,
            weekdayName: String,
            yearPersian: String,
            dayNumberPersian: String,
            monthName: String
        ) {
            views.setTextViewText(R.id.agenda_weekday, weekdayName)
            views.setTextViewText(R.id.agenda_year, yearPersian)
            views.setTextViewText(R.id.agenda_day_number, dayNumberPersian)
            views.setTextViewText(R.id.agenda_month_name, monthName)
        }

        private fun fetchAndFillOccasionToday(
            context: Context,
            todayJalali: com.vexono.app.domain.model.JalaliDate,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            views: RemoteViews
        ) {
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                try {
                    val db = VexonoDatabase.getDatabase(context)
                    val occasions = db.occasionDao().getOccasionsForDay(
                        todayJalali.year,
                        todayJalali.month,
                        todayJalali.day
                    ).firstOrNull()

                    if (!occasions.isNullOrEmpty()) {
                        val occasion = occasions.first()
                        views.setViewVisibility(R.id.widget_occasion, View.VISIBLE)
                        views.setTextViewText(R.id.widget_occasion, occasion.title)
                        if (occasion.isHoliday) {
                            views.setTextColor(R.id.widget_day_number, 0xFFFF5C7A.toInt())
                        } else {
                            views.setTextColor(R.id.widget_day_number, 0xFFF5F5F7.toInt())
                        }
                    } else {
                        views.setViewVisibility(R.id.widget_occasion, View.GONE)
                        views.setTextColor(R.id.widget_day_number, 0xFFF5F5F7.toInt())
                    }

                    // Also fetch today's events and tasks
                    val events = db.eventDao().getEventsForDay(
                        todayJalali.year,
                        todayJalali.month,
                        todayJalali.day
                    ).firstOrNull()

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching occasion data for widget", e)
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }

            // Do an immediate update without occasions (overridden by coroutine above)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun fetchAndFillAgendaData(
            context: Context,
            todayJalali: com.vexono.app.domain.model.JalaliDate,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            views: RemoteViews
        ) {
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                try {
                    val db = VexonoDatabase.getDatabase(context)

                    // Occasions
                    val occasions = db.occasionDao().getOccasionsForDay(
                        todayJalali.year, todayJalali.month, todayJalali.day
                    ).firstOrNull()

                    if (!occasions.isNullOrEmpty()) {
                        val occ = occasions.first()
                        views.setViewVisibility(R.id.agenda_occasion, View.VISIBLE)
                        views.setTextViewText(R.id.agenda_occasion, occ.title)
                    } else {
                        views.setViewVisibility(R.id.agenda_occasion, View.GONE)
                    }

                    // Events (show up to 2)
                    val events = db.eventDao().getEventsForDay(
                        todayJalali.year, todayJalali.month, todayJalali.day
                    ).firstOrNull() ?: emptyList()

                    if (events.isNotEmpty()) {
                        views.setViewVisibility(R.id.agenda_empty, View.GONE)

                        val event1 = events.getOrNull(0)
                        if (event1 != null) {
                            views.setViewVisibility(R.id.agenda_event_1_row, View.VISIBLE)
                            views.setTextViewText(R.id.agenda_event_1_title, event1.title)
                            views.setTextViewText(
                                R.id.agenda_event_1_time,
                                "${JalaliCalendarEngine.toPersianDigits(String.format("%02d", event1.hour))}:${JalaliCalendarEngine.toPersianDigits(String.format("%02d", event1.minute))}"
                            )
                            try {
                                val eventColor = Color.parseColor(event1.colorHex)
                                views.setInt(R.id.agenda_event_1_color, "setBackgroundColor", eventColor)
                            } catch (e: Exception) { /* ignore */ }
                        }

                        val event2 = events.getOrNull(1)
                        if (event2 != null) {
                            views.setViewVisibility(R.id.agenda_event_2_row, View.VISIBLE)
                            views.setTextViewText(R.id.agenda_event_2_title, event2.title)
                            views.setTextViewText(
                                R.id.agenda_event_2_time,
                                "${JalaliCalendarEngine.toPersianDigits(String.format("%02d", event2.hour))}:${JalaliCalendarEngine.toPersianDigits(String.format("%02d", event2.minute))}"
                            )
                            try {
                                val eventColor2 = Color.parseColor(event2.colorHex)
                                views.setInt(R.id.agenda_event_2_color, "setBackgroundColor", eventColor2)
                            } catch (e: Exception) { /* ignore */ }
                        }
                    } else if (occasions.isNullOrEmpty()) {
                        views.setViewVisibility(R.id.agenda_empty, View.VISIBLE)
                    }

                    // Tasks count
                    val tasks = db.taskDao().getTasksForDay(
                        todayJalali.year, todayJalali.month, todayJalali.day
                    ).firstOrNull() ?: emptyList()

                    if (tasks.isNotEmpty()) {
                        val completed = tasks.count { it.isCompleted }
                        views.setViewVisibility(R.id.agenda_task_summary, View.VISIBLE)
                        views.setTextViewText(
                            R.id.agenda_task_summary,
                            "✅ ${JalaliCalendarEngine.toPersianDigits(completed)} از ${JalaliCalendarEngine.toPersianDigits(tasks.size)} تسک"
                        )
                    }

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching agenda data for widget", e)
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

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
            fillBarWidget(views, weekdayName, dayNumberPersian, monthName)
            views.setOnClickPendingIntent(R.id.widget_bar_root, pendingIntent(context, 1))

            // Fetch occasion to set holiday color
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = VexonoDatabase.getDatabase(context)
                    val occasions = db.occasionDao().getOccasionsForDay(
                        todayJalali.year, todayJalali.month, todayJalali.day
                    ).firstOrNull()

                    if (!occasions.isNullOrEmpty() && occasions.first().isHoliday) {
                        views.setTextColor(R.id.bar_day_number, 0xFFFF5C7A.toInt())
                        views.setViewVisibility(R.id.bar_occasion, View.VISIBLE)
                        views.setTextViewText(R.id.bar_occasion, occasions.first().title)
                    }
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } catch (e: Exception) {
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateSquareWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val todayJalali = JalaliCalendarEngine.getTodayJalali()
            val todayGregorian = JalaliCalendarEngine.jalaliToGregorian(todayJalali)
            val dayOfWeekIndex = JalaliCalendarEngine.getDayOfWeek(todayJalali)
            val weekdayName = JalaliCalendarEngine.PERSIAN_WEEKDAY_NAMES.getOrElse(dayOfWeekIndex) { "" }
            val monthName = JalaliCalendarEngine.PERSIAN_MONTH_NAMES.getOrElse(todayJalali.month - 1) { "" }
            val dayNumberPersian = JalaliCalendarEngine.toPersianDigits(todayJalali.day)
            val yearPersian = JalaliCalendarEngine.toPersianDigits(todayJalali.year)
            val gregorianFormatted = JalaliCalendarEngine.getFullGregorianDateString(todayGregorian)

            val views = RemoteViews(context.packageName, R.layout.widget_vexono_square)
            fillSquareWidget(views, weekdayName, yearPersian, dayNumberPersian, monthName, gregorianFormatted)
            views.setOnClickPendingIntent(R.id.widget_square_root, pendingIntent(context, 2))

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = VexonoDatabase.getDatabase(context)
                    val occasions = db.occasionDao().getOccasionsForDay(
                        todayJalali.year, todayJalali.month, todayJalali.day
                    ).firstOrNull()

                    if (!occasions.isNullOrEmpty()) {
                        val occ = occasions.first()
                        views.setViewVisibility(R.id.square_occasion, View.VISIBLE)
                        views.setTextViewText(R.id.square_occasion, occ.title)
                        if (occ.isHoliday) {
                            views.setTextColor(R.id.square_day_number, 0xFFFF5C7A.toInt())
                        }
                    } else {
                        views.setViewVisibility(R.id.square_occasion, View.GONE)
                    }

                    val events = db.eventDao().getEventsForDay(
                        todayJalali.year, todayJalali.month, todayJalali.day
                    ).firstOrNull() ?: emptyList()

                    val tasks = db.taskDao().getTasksForDay(
                        todayJalali.year, todayJalali.month, todayJalali.day
                    ).firstOrNull() ?: emptyList()

                    if (events.isNotEmpty()) {
                        views.setViewVisibility(R.id.square_event_count, View.VISIBLE)
                        views.setTextViewText(
                            R.id.square_event_count,
                            "📅 ${JalaliCalendarEngine.toPersianDigits(events.size)} رویداد"
                        )
                    }

                    if (tasks.isNotEmpty()) {
                        views.setViewVisibility(R.id.square_task_count, View.VISIBLE)
                        val completed = tasks.count { it.isCompleted }
                        views.setTextViewText(
                            R.id.square_task_count,
                            "✅ ${JalaliCalendarEngine.toPersianDigits(completed)}/${JalaliCalendarEngine.toPersianDigits(tasks.size)}"
                        )
                    }

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } catch (e: Exception) {
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAgendaWidget(
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

            val views = RemoteViews(context.packageName, R.layout.widget_vexono_agenda)
            fillAgendaWidget(views, weekdayName, yearPersian, dayNumberPersian, monthName)
            views.setOnClickPendingIntent(R.id.widget_agenda_root, pendingIntent(context, 3))
            fetchAndFillAgendaData(context, todayJalali, appWidgetManager, appWidgetId, views)
        }

        fun sendUpdateBroadcast(context: Context) {
            // Update original/today widget
            val intent = Intent(context, VexonoWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, VexonoWidgetProvider::class.java))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)

            // Update bar widget
            val barIds = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, VexonoBarWidgetProvider::class.java))
            if (barIds.isNotEmpty()) {
                val barIntent = Intent(context, VexonoBarWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, barIds)
                }
                context.sendBroadcast(barIntent)
            }

            // Update square widget
            val squareIds = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, VexonoSquareWidgetProvider::class.java))
            if (squareIds.isNotEmpty()) {
                val squareIntent = Intent(context, VexonoSquareWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, squareIds)
                }
                context.sendBroadcast(squareIntent)
            }

            // Update agenda widget
            val agendaIds = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, VexonoAgendaWidgetProvider::class.java))
            if (agendaIds.isNotEmpty()) {
                val agendaIntent = Intent(context, VexonoAgendaWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, agendaIds)
                }
                context.sendBroadcast(agendaIntent)
            }
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
        for (id in appWidgetIds) {
            VexonoWidgetProvider.updateBarWidget(context, appWidgetManager, id)
        }
    }
    override fun onEnabled(context: Context) { MidnightUpdateReceiver.schedule(context) }
    override fun onDisabled(context: Context) { /* only cancel if ALL providers are gone */ }
}

/** Square (2×2) Widget Provider */
class VexonoSquareWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            VexonoWidgetProvider.updateSquareWidget(context, appWidgetManager, id)
        }
    }
    override fun onEnabled(context: Context) { MidnightUpdateReceiver.schedule(context) }
    override fun onDisabled(context: Context) { /* only cancel if ALL providers are gone */ }
}

/** Agenda (4×2) Widget Provider */
class VexonoAgendaWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            VexonoWidgetProvider.updateAgendaWidget(context, appWidgetManager, id)
        }
    }
    override fun onEnabled(context: Context) { MidnightUpdateReceiver.schedule(context) }
    override fun onDisabled(context: Context) { /* only cancel if ALL providers are gone */ }
}
