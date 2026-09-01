package com.vexono.app.di

import android.content.Context
import com.vexono.app.data.datastore.PreferencesDataStore
import com.vexono.app.data.local.database.VexonoDatabase
import com.vexono.app.data.repository.EventRepositoryImpl
import com.vexono.app.data.repository.OccasionRepositoryImpl
import com.vexono.app.data.repository.SettingsRepositoryImpl
import com.vexono.app.data.repository.TaskRepositoryImpl
import com.vexono.app.domain.repository.EventRepository
import com.vexono.app.domain.repository.OccasionRepository
import com.vexono.app.domain.repository.SettingsRepository
import com.vexono.app.domain.repository.TaskRepository
import com.vexono.app.domain.usecase.AddEventUseCase
import com.vexono.app.domain.usecase.AddTaskUseCase
import com.vexono.app.domain.usecase.DeleteEventUseCase
import com.vexono.app.domain.usecase.DeleteTaskUseCase
import com.vexono.app.domain.usecase.GetAllEventsUseCase
import com.vexono.app.domain.usecase.GetAllTasksUseCase
import com.vexono.app.domain.usecase.GetEventsForDayUseCase
import com.vexono.app.domain.usecase.GetMonthCalendarUseCase
import com.vexono.app.domain.usecase.GetOccasionsForDayUseCase
import com.vexono.app.domain.usecase.GetOccasionsForYearUseCase
import com.vexono.app.domain.usecase.GetSettingsUseCase
import com.vexono.app.domain.usecase.GetTasksForDayUseCase
import com.vexono.app.domain.usecase.SearchOccasionsUseCase
import com.vexono.app.domain.usecase.ToggleTaskUseCase
import com.vexono.app.domain.usecase.UpdateEventUseCase
import com.vexono.app.domain.usecase.UpdateSettingsUseCase

class AppContainer(private val context: Context) {

    private val database: VexonoDatabase by lazy {
        VexonoDatabase.getDatabase(context)
    }

    private val preferencesDataStore: PreferencesDataStore by lazy {
        PreferencesDataStore(context)
    }

    // Repositories
    val eventRepository: EventRepository by lazy {
        EventRepositoryImpl(database.eventDao())
    }

    val taskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(database.taskDao())
    }

    val occasionRepository: OccasionRepository by lazy {
        OccasionRepositoryImpl(context, database.occasionDao())
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(preferencesDataStore)
    }

    // UseCases
    val getMonthCalendarUseCase: GetMonthCalendarUseCase by lazy {
        GetMonthCalendarUseCase(occasionRepository, eventRepository, taskRepository)
    }

    val getOccasionsForDayUseCase: GetOccasionsForDayUseCase by lazy {
        GetOccasionsForDayUseCase(occasionRepository)
    }

    val getOccasionsForYearUseCase: GetOccasionsForYearUseCase by lazy {
        GetOccasionsForYearUseCase(occasionRepository)
    }

    val searchOccasionsUseCase: SearchOccasionsUseCase by lazy {
        SearchOccasionsUseCase(occasionRepository)
    }

    val getEventsForDayUseCase: GetEventsForDayUseCase by lazy {
        GetEventsForDayUseCase(eventRepository)
    }

    val getAllEventsUseCase: GetAllEventsUseCase by lazy {
        GetAllEventsUseCase(eventRepository)
    }

    val addEventUseCase: AddEventUseCase by lazy {
        AddEventUseCase(eventRepository)
    }

    val updateEventUseCase: UpdateEventUseCase by lazy {
        UpdateEventUseCase(eventRepository)
    }

    val deleteEventUseCase: DeleteEventUseCase by lazy {
        DeleteEventUseCase(eventRepository)
    }

    val getTasksForDayUseCase: GetTasksForDayUseCase by lazy {
        GetTasksForDayUseCase(taskRepository)
    }

    val getAllTasksUseCase: GetAllTasksUseCase by lazy {
        GetAllTasksUseCase(taskRepository)
    }

    val addTaskUseCase: AddTaskUseCase by lazy {
        AddTaskUseCase(taskRepository)
    }

    val toggleTaskUseCase: ToggleTaskUseCase by lazy {
        ToggleTaskUseCase(taskRepository)
    }

    val deleteTaskUseCase: DeleteTaskUseCase by lazy {
        DeleteTaskUseCase(taskRepository)
    }

    val getSettingsUseCase: GetSettingsUseCase by lazy {
        GetSettingsUseCase(settingsRepository)
    }

    val updateSettingsUseCase: UpdateSettingsUseCase by lazy {
        UpdateSettingsUseCase(settingsRepository)
    }
}
