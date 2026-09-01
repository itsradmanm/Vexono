package com.vexono.app.domain.usecase

import com.vexono.app.domain.model.Event
import com.vexono.app.domain.model.Occasion
import com.vexono.app.domain.model.Task
import com.vexono.app.domain.model.ThemeMode
import com.vexono.app.domain.model.UserSettings
import com.vexono.app.domain.repository.EventRepository
import com.vexono.app.domain.repository.OccasionRepository
import com.vexono.app.domain.repository.SettingsRepository
import com.vexono.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

// Occasion Use Cases
class GetOccasionsForDayUseCase(private val occasionRepository: OccasionRepository) {
    operator fun invoke(year: Int, month: Int, day: Int): Flow<List<Occasion>> {
        return occasionRepository.getOccasionsForDay(year, month, day)
    }
}

class GetOccasionsForYearUseCase(private val occasionRepository: OccasionRepository) {
    operator fun invoke(year: Int): Flow<List<Occasion>> {
        return occasionRepository.getOccasionsForYear(year)
    }
}

class SearchOccasionsUseCase(private val occasionRepository: OccasionRepository) {
    operator fun invoke(query: String): Flow<List<Occasion>> {
        return occasionRepository.searchOccasions(query)
    }
}

// Event Use Cases
class GetEventsForDayUseCase(private val eventRepository: EventRepository) {
    operator fun invoke(year: Int, month: Int, day: Int): Flow<List<Event>> {
        return eventRepository.getEventsForDay(year, month, day)
    }
}

class GetAllEventsUseCase(private val eventRepository: EventRepository) {
    operator fun invoke(): Flow<List<Event>> {
        return eventRepository.getAllEvents()
    }
}

class AddEventUseCase(private val eventRepository: EventRepository) {
    suspend operator fun invoke(event: Event): Long {
        return eventRepository.addEvent(event)
    }
}

class UpdateEventUseCase(private val eventRepository: EventRepository) {
    suspend operator fun invoke(event: Event) {
        eventRepository.updateEvent(event)
    }
}

class DeleteEventUseCase(private val eventRepository: EventRepository) {
    suspend operator fun invoke(id: Long) {
        eventRepository.deleteEventById(id)
    }
}

// Task Use Cases
class GetTasksForDayUseCase(private val taskRepository: TaskRepository) {
    operator fun invoke(year: Int, month: Int, day: Int): Flow<List<Task>> {
        return taskRepository.getTasksForDay(year, month, day)
    }
}

class GetAllTasksUseCase(private val taskRepository: TaskRepository) {
    operator fun invoke(): Flow<List<Task>> {
        return taskRepository.getAllTasks()
    }
}

class AddTaskUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(task: Task): Long {
        return taskRepository.addTask(task)
    }
}

class ToggleTaskUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(id: Long, isCompleted: Boolean) {
        taskRepository.toggleTaskCompletion(id, isCompleted)
    }
}

class DeleteTaskUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(id: Long) {
        taskRepository.deleteTaskById(id)
    }
}

// Settings Use Cases
class GetSettingsUseCase(private val settingsRepository: SettingsRepository) {
    operator fun invoke(): Flow<UserSettings> {
        return settingsRepository.userSettings
    }
}

class UpdateSettingsUseCase(private val settingsRepository: SettingsRepository) {
    suspend fun setThemeMode(mode: ThemeMode) = settingsRepository.updateThemeMode(mode)
    suspend fun setPrimaryColor(colorHex: String) = settingsRepository.updatePrimaryColorHex(colorHex)
    suspend fun setShowGregorianDate(show: Boolean) = settingsRepository.updateShowGregorianDate(show)
    suspend fun setShowIslamicDate(show: Boolean) = settingsRepository.updateShowIslamicDate(show)
    suspend fun setEnableNotifications(enable: Boolean) = settingsRepository.updateEnableNotifications(enable)
}
