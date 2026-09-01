package com.vexono.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.vexono.app.domain.model.Event
import com.vexono.app.domain.model.Occasion
import com.vexono.app.domain.model.OccasionCategory
import com.vexono.app.domain.model.Priority
import com.vexono.app.domain.model.RecurrenceType
import com.vexono.app.domain.model.Task

@Entity(
    tableName = "events",
    indices = [
        Index(value = ["jalaliYear", "jalaliMonth", "jalaliDay"]),
        Index(value = ["jalaliMonth", "jalaliDay"])
    ]
)
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val jalaliYear: Int,
    val jalaliMonth: Int,
    val jalaliDay: Int,
    val hour: Int = 12,
    val minute: Int = 0,
    val colorHex: String = "#7C4DFF",
    val recurrence: String = RecurrenceType.NONE.name,
    val hasReminder: Boolean = true,
    val reminderMinutesBefore: Int = 15,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Event {
        return Event(
            id = id,
            title = title,
            description = description,
            jalaliYear = jalaliYear,
            jalaliMonth = jalaliMonth,
            jalaliDay = jalaliDay,
            hour = hour,
            minute = minute,
            colorHex = colorHex,
            recurrence = runCatching { RecurrenceType.valueOf(recurrence) }.getOrDefault(RecurrenceType.NONE),
            hasReminder = hasReminder,
            reminderMinutesBefore = reminderMinutesBefore,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(event: Event): EventEntity {
            return EventEntity(
                id = event.id,
                title = event.title,
                description = event.description,
                jalaliYear = event.jalaliYear,
                jalaliMonth = event.jalaliMonth,
                jalaliDay = event.jalaliDay,
                hour = event.hour,
                minute = event.minute,
                colorHex = event.colorHex,
                recurrence = event.recurrence.name,
                hasReminder = event.hasReminder,
                reminderMinutesBefore = event.reminderMinutesBefore,
                createdAt = event.createdAt
            )
        }
    }
}

@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["jalaliYear", "jalaliMonth", "jalaliDay"]),
        Index(value = ["isCompleted"])
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val jalaliYear: Int,
    val jalaliMonth: Int,
    val jalaliDay: Int,
    val isCompleted: Boolean = false,
    val priority: String = Priority.MEDIUM.name,
    val category: String = "عمومی",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Task {
        return Task(
            id = id,
            title = title,
            jalaliYear = jalaliYear,
            jalaliMonth = jalaliMonth,
            jalaliDay = jalaliDay,
            isCompleted = isCompleted,
            priority = runCatching { Priority.valueOf(priority) }.getOrDefault(Priority.MEDIUM),
            category = category,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(task: Task): TaskEntity {
            return TaskEntity(
                id = task.id,
                title = task.title,
                jalaliYear = task.jalaliYear,
                jalaliMonth = task.jalaliMonth,
                jalaliDay = task.jalaliDay,
                isCompleted = task.isCompleted,
                priority = task.priority.name,
                category = task.category,
                createdAt = task.createdAt
            )
        }
    }
}

@Entity(
    tableName = "occasions",
    indices = [
        Index(value = ["month", "day"]),
        Index(value = ["year", "month", "day"]),
        Index(value = ["isHoliday"])
    ]
)
data class OccasionEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val isHoliday: Boolean,
    val category: String,
    val month: Int,
    val day: Int,
    val year: Int? = null
) {
    fun toDomain(): Occasion {
        return Occasion(
            id = id,
            title = title,
            isHoliday = isHoliday,
            category = runCatching { OccasionCategory.valueOf(category) }.getOrDefault(OccasionCategory.NATIONAL),
            month = month,
            day = day,
            year = year
        )
    }

    companion object {
        fun fromDomain(occasion: Occasion): OccasionEntity {
            return OccasionEntity(
                id = occasion.id,
                title = occasion.title,
                isHoliday = occasion.isHoliday,
                category = occasion.category.name,
                month = occasion.month,
                day = occasion.day,
                year = occasion.year
            )
        }
    }
}
