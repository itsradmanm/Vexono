package com.vexono.app.data.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
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

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_vexono_today)

            val todayJalali = JalaliCalendarEngine.getTodayJalali()
            val todayGregorian = JalaliCalendarEngine.jalaliToGregorian(todayJalali)
            val dayOfWeekIndex = JalaliCalendarEngine.getDayOfWeek(todayJalali)

            val weekdayName = JalaliCalendarEngine.PERSIAN_WEEKDAY_NAMES.getOrElse(dayOfWeekIndex) { "" }
            val monthName = JalaliCalendarEngine.PERSIAN_MONTH_NAMES.getOrElse(todayJalali.month - 1) { "" }
            val dayNumberPersian = JalaliCalendarEngine.toPersianDigits(todayJalali.day)
            val yearPersian = JalaliCalendarEngine.toPersianDigits(todayJalali.year)
            val gregorianFormatted = JalaliCalendarEngine.getFullGregorianDateString(todayGregorian)

            views.setTextViewText(R.id.widget_weekday, weekdayName)
            views.setTextViewText(R.id.widget_year, yearPersian)
            views.setTextViewText(R.id.widget_day_number, dayNumberPersian)
            views.setTextViewText(R.id.widget_month_name, monthName)
            views.setTextViewText(R.id.widget_gregorian_date, gregorianFormatted)

            // Tap on widget opens app
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            // Fetch occasions for today asynchronously
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
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
                    }
                } else {
                    views.setViewVisibility(R.id.widget_occasion, View.GONE)
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun sendUpdateBroadcast(context: Context) {
            val intent = Intent(context, VexonoWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, VexonoWidgetProvider::class.java))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }
}
