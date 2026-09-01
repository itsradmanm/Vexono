package com.vexono.app.domain.repository

import com.vexono.app.domain.model.Event
import com.vexono.app.domain.model.Occasion
import com.vexono.app.domain.model.Task
import com.vexono.app.domain.model.ThemeMode
import com.vexono.app.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getAllEvents(): Flow<List<Event>>
    fun getEventsForDay(year: Int, month: Int, day: Int): Flow<List<Event>>
    fun getEventsForMonth(year: Int, month: Int): Flow<List<Event>>
    suspend fun getEventById(id: Long): Event?
    suspend fun addEvent(event: Event): Long
    suspend fun updateEvent(event: Event)
    suspend fun deleteEvent(event: Event)
    suspend fun deleteEventById(id: Long)
}

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getTasksForDay(year: Int, month: Int, day: Int): Flow<List<Task>>
    fun getTasksForMonth(year: Int, month: Int): Flow<List<Task>>
    suspend fun getTaskById(id: Long): Task?
    suspend fun addTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun toggleTaskCompletion(id: Long, isCompleted: Boolean)
    suspend fun deleteTask(task: Task)
    suspend fun deleteTaskById(id: Long)
}

interface OccasionRepository {
    fun getOccasionsForDay(year: Int, month: Int, day: Int): Flow<List<Occasion>>
    fun getOccasionsForMonth(year: Int, month: Int): Flow<List<Occasion>>
    fun getOccasionsForYear(year: Int): Flow<List<Occasion>>
    fun searchOccasions(query: String): Flow<List<Occasion>>
    suspend fun ensureOccasionsLoaded()
}

interface SettingsRepository {
    val userSettings: Flow<UserSettings>
    suspend fun updateThemeMode(themeMode: ThemeMode)
    suspend fun updatePrimaryColorHex(colorHex: String)
    suspend fun updateShowGregorianDate(show: Boolean)
    suspend fun updateShowIslamicDate(show: Boolean)
    suspend fun updateEnableNotifications(enable: Boolean)
}
