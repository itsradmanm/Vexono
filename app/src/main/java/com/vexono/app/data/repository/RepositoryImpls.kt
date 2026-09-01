package com.vexono.app.data.repository

import android.content.Context
import com.vexono.app.data.datastore.PreferencesDataStore
import com.vexono.app.data.local.dao.EventDao
import com.vexono.app.data.local.dao.OccasionDao
import com.vexono.app.data.local.dao.TaskDao
import com.vexono.app.data.local.database.VexonoDatabase
import com.vexono.app.data.local.entity.EventEntity
import com.vexono.app.data.local.entity.TaskEntity
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
import kotlinx.coroutines.flow.map

class EventRepositoryImpl(
    private val eventDao: EventDao
) : EventRepository {

    override fun getAllEvents(): Flow<List<Event>> {
        return eventDao.getAllEvents().map { list -> list.map { it.toDomain() } }
    }

    override fun getEventsForDay(year: Int, month: Int, day: Int): Flow<List<Event>> {
        return eventDao.getEventsForDay(year, month, day).map { list -> list.map { it.toDomain() } }
    }

    override fun getEventsForMonth(year: Int, month: Int): Flow<List<Event>> {
        return eventDao.getEventsForMonth(year, month).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getEventById(id: Long): Event? {
        return eventDao.getEventById(id)?.toDomain()
    }

    override suspend fun addEvent(event: Event): Long {
        return eventDao.insertEvent(EventEntity.fromDomain(event))
    }

    override suspend fun updateEvent(event: Event) {
        eventDao.updateEvent(EventEntity.fromDomain(event))
    }

    override suspend fun deleteEvent(event: Event) {
        eventDao.deleteEvent(EventEntity.fromDomain(event))
    }

    override suspend fun deleteEventById(id: Long) {
        eventDao.deleteEventById(id)
    }
}

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { list -> list.map { it.toDomain() } }
    }

    override fun getTasksForDay(year: Int, month: Int, day: Int): Flow<List<Task>> {
        return taskDao.getTasksForDay(year, month, day).map { list -> list.map { it.toDomain() } }
    }

    override fun getTasksForMonth(year: Int, month: Int): Flow<List<Task>> {
        return taskDao.getTasksForMonth(year, month).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getTaskById(id: Long): Task? {
        return taskDao.getTaskById(id)?.toDomain()
    }

    override suspend fun addTask(task: Task): Long {
        return taskDao.insertTask(TaskEntity.fromDomain(task))
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(TaskEntity.fromDomain(task))
    }

    override suspend fun toggleTaskCompletion(id: Long, isCompleted: Boolean) {
        taskDao.updateTaskCompletion(id, isCompleted)
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(TaskEntity.fromDomain(task))
    }

    override suspend fun deleteTaskById(id: Long) {
        taskDao.deleteTaskById(id)
    }
}

class OccasionRepositoryImpl(
    private val context: Context,
    private val occasionDao: OccasionDao
) : OccasionRepository {

    override fun getOccasionsForDay(year: Int, month: Int, day: Int): Flow<List<Occasion>> {
        return occasionDao.getOccasionsForDay(year, month, day).map { list -> list.map { it.toDomain() } }
    }

    override fun getOccasionsForMonth(year: Int, month: Int): Flow<List<Occasion>> {
        return occasionDao.getOccasionsForMonth(year, month).map { list -> list.map { it.toDomain() } }
    }

    override fun getOccasionsForYear(year: Int): Flow<List<Occasion>> {
        return occasionDao.getOccasionsForYear(year).map { list -> list.map { it.toDomain() } }
    }

    override fun searchOccasions(query: String): Flow<List<Occasion>> {
        return occasionDao.searchOccasions(query).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun ensureOccasionsLoaded() {
        VexonoDatabase.populateOccasionsFromAssets(context, occasionDao)
    }
}

class SettingsRepositoryImpl(
    private val preferencesDataStore: PreferencesDataStore
) : SettingsRepository {

    override val userSettings: Flow<UserSettings> = preferencesDataStore.userSettingsFlow

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        preferencesDataStore.updateThemeMode(themeMode)
    }

    override suspend fun updatePrimaryColorHex(colorHex: String) {
        preferencesDataStore.updatePrimaryColorHex(colorHex)
    }

    override suspend fun updateShowGregorianDate(show: Boolean) {
        preferencesDataStore.updateShowGregorianDate(show)
    }

    override suspend fun updateShowIslamicDate(show: Boolean) {
        preferencesDataStore.updateShowIslamicDate(show)
    }

    override suspend fun updateEnableNotifications(enable: Boolean) {
        preferencesDataStore.updateEnableNotifications(enable)
    }
}
