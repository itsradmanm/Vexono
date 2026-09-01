package com.vexono.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Calendar : Screen("calendar")
    data object DayDetail : Screen("day_detail/{year}/{month}/{day}") {
        fun createRoute(year: Int, month: Int, day: Int) = "day_detail/$year/$month/$day"
    }
    data object EventEditor : Screen("event_editor?eventId={eventId}&year={year}&month={month}&day={day}") {
        fun createRoute(eventId: Long = 0L, year: Int = 0, month: Int = 0, day: Int = 0) =
            "event_editor?eventId=$eventId&year=$year&month=$month&day=$day"
    }
    data object Tasks : Screen("tasks")
    data object Occasions : Screen("occasions")
    data object Settings : Screen("settings")
}

enum class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    CALENDAR(Screen.Calendar.route, "تقویم", Icons.Default.CalendarMonth),
    TASKS(Screen.Tasks.route, "تسک‌ها", Icons.Default.Checklist),
    OCCASIONS(Screen.Occasions.route, "مناسبت‌ها", Icons.Default.EventNote),
    SETTINGS(Screen.Settings.route, "تنظیمات", Icons.Default.Settings)
}
