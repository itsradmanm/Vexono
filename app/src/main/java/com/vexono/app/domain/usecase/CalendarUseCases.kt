package com.vexono.app.domain.usecase

import com.vexono.app.data.calendar.JalaliCalendarEngine
import com.vexono.app.domain.model.CalendarDay
import com.vexono.app.domain.model.Event
import com.vexono.app.domain.model.JalaliDate
import com.vexono.app.domain.model.Occasion
import com.vexono.app.domain.model.Task
import com.vexono.app.domain.repository.EventRepository
import com.vexono.app.domain.repository.OccasionRepository
import com.vexono.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetMonthCalendarUseCase(
    private val occasionRepository: OccasionRepository,
    private val eventRepository: EventRepository,
    private val taskRepository: TaskRepository
) {
    operator fun invoke(year: Int, month: Int): Flow<List<CalendarDay>> {
        val occasionsFlow = occasionRepository.getOccasionsForMonth(year, month)
        val eventsFlow = eventRepository.getEventsForMonth(year, month)
        val tasksFlow = taskRepository.getTasksForMonth(year, month)

        return combine(occasionsFlow, eventsFlow, tasksFlow) { occasions, events, tasks ->
            buildCalendarGrid(year, month, occasions, events, tasks)
        }
    }

    private fun buildCalendarGrid(
        year: Int,
        month: Int,
        occasions: List<Occasion>,
        events: List<Event>,
        tasks: List<Task>
    ): List<CalendarDay> {
        val today = JalaliCalendarEngine.getTodayJalali()
        val daysInCurrentMonth = JalaliCalendarEngine.getDaysInJalaliMonth(year, month)
        val firstDayOfMonth = JalaliDate(year, month, 1)
        val startDayOfWeek = JalaliCalendarEngine.getDayOfWeek(firstDayOfMonth) // 0 (Sat) to 6 (Fri)

        val grid = mutableListOf<CalendarDay>()

        // 1. Previous Month Days Padding
        if (startDayOfWeek > 0) {
            val prevMonth = if (month == 1) 12 else month - 1
            val prevYear = if (month == 1) year - 1 else year
            val daysInPrevMonth = JalaliCalendarEngine.getDaysInJalaliMonth(prevYear, prevMonth)

            val startPrevDay = daysInPrevMonth - startDayOfWeek + 1
            for (d in startPrevDay..daysInPrevMonth) {
                val jDate = JalaliDate(prevYear, prevMonth, d)
                val gDate = JalaliCalendarEngine.jalaliToGregorian(jDate)
                val iDate = JalaliCalendarEngine.jalaliToIslamic(jDate)
                val dow = JalaliCalendarEngine.getDayOfWeek(jDate)
                grid.add(
                    CalendarDay(
                        jalaliDate = jDate,
                        gregorianDate = gDate,
                        islamicDate = iDate,
                        isCurrentMonth = false,
                        isToday = (jDate == today),
                        isHoliday = (dow == 6),
                        isFriday = (dow == 6),
                        dayOfWeek = dow
                    )
                )
            }
        }

        // 2. Current Month Days
        for (d in 1..daysInCurrentMonth) {
            val jDate = JalaliDate(year, month, d)
            val gDate = JalaliCalendarEngine.jalaliToGregorian(jDate)
            val iDate = JalaliCalendarEngine.jalaliToIslamic(jDate)
            val dow = JalaliCalendarEngine.getDayOfWeek(jDate)
            val isFriday = (dow == 6)

            val dayOccasions = occasions.filter { it.month == month && it.day == d }
            val isHoliday = isFriday || dayOccasions.any { it.isHoliday }

            val dayEvents = events.filter { event ->
                (event.jalaliYear == year && event.jalaliMonth == month && event.jalaliDay == d) ||
                        (event.recurrence.name == "DAILY") ||
                        (event.recurrence.name == "WEEKLY" && JalaliCalendarEngine.getDayOfWeek(JalaliDate(event.jalaliYear, event.jalaliMonth, event.jalaliDay)) == dow) ||
                        (event.recurrence.name == "MONTHLY" && event.jalaliDay == d) ||
                        (event.recurrence.name == "YEARLY" && event.jalaliMonth == month && event.jalaliDay == d)
            }

            val dayTasks = tasks.filter { it.jalaliYear == year && it.jalaliMonth == month && it.jalaliDay == d }
            val completedTasks = dayTasks.count { it.isCompleted }

            grid.add(
                CalendarDay(
                    jalaliDate = jDate,
                    gregorianDate = gDate,
                    islamicDate = iDate,
                    isCurrentMonth = true,
                    isToday = (jDate == today),
                    isHoliday = isHoliday,
                    isFriday = isFriday,
                    occasions = dayOccasions,
                    eventCount = dayEvents.size,
                    taskCount = dayTasks.size,
                    completedTaskCount = completedTasks,
                    dayOfWeek = dow
                )
            )
        }

        // 3. Next Month Days Padding to complete grid to multiple of 7
        val remaining = (7 - (grid.size % 7)) % 7
        if (remaining > 0) {
            val nextMonth = if (month == 12) 1 else month + 1
            val nextYear = if (month == 12) year + 1 else year
            for (d in 1..remaining) {
                val jDate = JalaliDate(nextYear, nextMonth, d)
                val gDate = JalaliCalendarEngine.jalaliToGregorian(jDate)
                val iDate = JalaliCalendarEngine.jalaliToIslamic(jDate)
                val dow = JalaliCalendarEngine.getDayOfWeek(jDate)
                grid.add(
                    CalendarDay(
                        jalaliDate = jDate,
                        gregorianDate = gDate,
                        islamicDate = iDate,
                        isCurrentMonth = false,
                        isToday = (jDate == today),
                        isHoliday = (dow == 6),
                        isFriday = (dow == 6),
                        dayOfWeek = dow
                    )
                )
            }
        }

        return grid
    }
}
