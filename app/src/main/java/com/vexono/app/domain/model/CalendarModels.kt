package com.vexono.app.domain.model

enum class ThemeMode {
    DARK, LIGHT, SYSTEM
}

enum class RecurrenceType {
    NONE, DAILY, WEEKLY, MONTHLY, YEARLY
}

enum class Priority {
    LOW, MEDIUM, HIGH
}

enum class OccasionCategory {
    NATIONAL, RELIGIOUS, OFFICIAL, INTERNATIONAL
}

data class JalaliDate(
    val year: Int,
    val month: Int,
    val day: Int
) : Comparable<JalaliDate> {
    override fun compareTo(other: JalaliDate): Int {
        if (year != other.year) return year.compareTo(other.year)
        if (month != other.month) return month.compareTo(other.month)
        return day.compareTo(other.day)
    }

    override fun toString(): String = String.format("%04d/%02d/%02d", year, month, day)
}

data class GregorianDate(
    val year: Int,
    val month: Int,
    val day: Int
) : Comparable<GregorianDate> {
    override fun compareTo(other: GregorianDate): Int {
        if (year != other.year) return year.compareTo(other.year)
        if (month != other.month) return month.compareTo(other.month)
        return day.compareTo(other.day)
    }

    override fun toString(): String = String.format("%04d-%02d-%02d", year, month, day)
}

data class IslamicDate(
    val year: Int,
    val month: Int,
    val day: Int
) {
    override fun toString(): String = String.format("%04d/%02d/%02d", year, month, day)
}

data class Occasion(
    val id: String,
    val title: String,
    val isHoliday: Boolean,
    val category: OccasionCategory,
    val month: Int,
    val day: Int,
    val year: Int? = null
)

data class CalendarDay(
    val jalaliDate: JalaliDate,
    val gregorianDate: GregorianDate,
    val islamicDate: IslamicDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isHoliday: Boolean,
    val isFriday: Boolean,
    val occasions: List<Occasion> = emptyList(),
    val eventCount: Int = 0,
    val taskCount: Int = 0,
    val completedTaskCount: Int = 0,
    val dayOfWeek: Int // 0: Saturday (شنبه) to 6: Friday (جمعه)
)

data class Event(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val jalaliYear: Int,
    val jalaliMonth: Int,
    val jalaliDay: Int,
    val hour: Int = 12,
    val minute: Int = 0,
    val colorHex: String = "#7C4DFF",
    val recurrence: RecurrenceType = RecurrenceType.NONE,
    val hasReminder: Boolean = true,
    val reminderMinutesBefore: Int = 15,
    val createdAt: Long = System.currentTimeMillis()
)

data class Task(
    val id: Long = 0,
    val title: String,
    val jalaliYear: Int,
    val jalaliMonth: Int,
    val jalaliDay: Int,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val category: String = "عمومی",
    val createdAt: Long = System.currentTimeMillis()
)

data class UserSettings(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val primaryColorHex: String = "#7C4DFF",
    val showGregorianDate: Boolean = true,
    val showIslamicDate: Boolean = true,
    val enableNotifications: Boolean = true
)
