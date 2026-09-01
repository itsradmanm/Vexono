package com.vexono.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vexono.app.data.calendar.JalaliCalendarEngine
import com.vexono.app.di.AppContainer
import com.vexono.app.domain.model.CalendarDay
import com.vexono.app.domain.model.Event
import com.vexono.app.domain.model.JalaliDate
import com.vexono.app.domain.model.Occasion
import com.vexono.app.domain.model.OccasionCategory
import com.vexono.app.domain.model.Priority
import com.vexono.app.domain.model.RecurrenceType
import com.vexono.app.domain.model.Task
import com.vexono.app.domain.model.ThemeMode
import com.vexono.app.domain.model.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ----------------------------------------------------
// 1. Calendar Home ViewModel
// ----------------------------------------------------
data class CalendarUiState(
    val currentYear: Int,
    val currentMonth: Int,
    val selectedDay: CalendarDay? = null,
    val days: List<CalendarDay> = emptyList(),
    val userSettings: UserSettings = UserSettings(),
    val isLoading: Boolean = false
)

class CalendarViewModel(
    private val container: AppContainer
) : ViewModel() {

    private val today = JalaliCalendarEngine.getTodayJalali()
    private val _currentYear = MutableStateFlow(today.year)
    private val _currentMonth = MutableStateFlow(today.month)
    private val _selectedDay = MutableStateFlow<CalendarDay?>(null)

    val uiState: StateFlow<CalendarUiState> = combine(
        _currentYear,
        _currentMonth,
        _selectedDay,
        container.getSettingsUseCase()
    ) { year, month, selectedDay, settings ->
        Triple(Pair(year, month), selectedDay, settings)
    }.flatMapLatest { (yearMonth, selectedDay, settings) ->
        val (year, month) = yearMonth
        container.getMonthCalendarUseCase(year, month).map { days ->
            val activeSelectedDay = selectedDay ?: days.find { it.isToday } ?: days.firstOrNull { it.isCurrentMonth }
            CalendarUiState(
                currentYear = year,
                currentMonth = month,
                selectedDay = activeSelectedDay,
                days = days,
                userSettings = settings,
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarUiState(today.year, today.month)
    )

    fun nextMonth() {
        if (_currentMonth.value == 12) {
            _currentYear.value += 1
            _currentMonth.value = 1
        } else {
            _currentMonth.value += 1
        }
        _selectedDay.value = null
    }

    fun prevMonth() {
        if (_currentMonth.value == 1) {
            _currentYear.value -= 1
            _currentMonth.value = 12
        } else {
            _currentMonth.value -= 1
        }
        _selectedDay.value = null
    }

    fun jumpToToday() {
        val now = JalaliCalendarEngine.getTodayJalali()
        _currentYear.value = now.year
        _currentMonth.value = now.month
        _selectedDay.value = null
    }

    fun setYearMonth(year: Int, month: Int) {
        _currentYear.value = year
        _currentMonth.value = month
        _selectedDay.value = null
    }

    fun selectDay(day: CalendarDay) {
        _selectedDay.value = day
    }
}

// ----------------------------------------------------
// 2. Day Detail ViewModel
// ----------------------------------------------------
data class DayDetailUiState(
    val jalaliDate: JalaliDate,
    val occasions: List<Occasion> = emptyList(),
    val events: List<Event> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val userSettings: UserSettings = UserSettings()
)

class DayDetailViewModel(
    private val date: JalaliDate,
    private val container: AppContainer
) : ViewModel() {

    val uiState: StateFlow<DayDetailUiState> = combine(
        container.getOccasionsForDayUseCase(date.year, date.month, date.day),
        container.getEventsForDayUseCase(date.year, date.month, date.day),
        container.getTasksForDayUseCase(date.year, date.month, date.day),
        container.getSettingsUseCase()
    ) { occasions, events, tasks, settings ->
        DayDetailUiState(
            jalaliDate = date,
            occasions = occasions,
            events = events,
            tasks = tasks,
            userSettings = settings
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DayDetailUiState(jalaliDate = date)
    )

    fun toggleTask(id: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            container.toggleTaskUseCase(id, isCompleted)
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            container.deleteTaskUseCase(id)
        }
    }

    fun deleteEvent(id: Long) {
        viewModelScope.launch {
            container.deleteEventUseCase(id)
        }
    }
}

// ----------------------------------------------------
// 3. Event Editor ViewModel
// ----------------------------------------------------
data class EventEditorUiState(
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val jalaliYear: Int = 1403,
    val jalaliMonth: Int = 1,
    val jalaliDay: Int = 1,
    val hour: Int = 12,
    val minute: Int = 0,
    val colorHex: String = "#7C4DFF",
    val recurrence: RecurrenceType = RecurrenceType.NONE,
    val hasReminder: Boolean = true,
    val reminderMinutesBefore: Int = 15,
    val isSaved: Boolean = false,
    val error: String? = null
)

class EventEditorViewModel(
    private val eventId: Long?,
    initialDate: JalaliDate?,
    private val container: AppContainer
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        EventEditorUiState(
            jalaliYear = initialDate?.year ?: JalaliCalendarEngine.getTodayJalali().year,
            jalaliMonth = initialDate?.month ?: JalaliCalendarEngine.getTodayJalali().month,
            jalaliDay = initialDate?.day ?: JalaliCalendarEngine.getTodayJalali().day
        )
    )
    val uiState: StateFlow<EventEditorUiState> = _uiState.asStateFlow()

    init {
        if (eventId != null && eventId > 0) {
            viewModelScope.launch {
                val event = container.eventRepository.getEventById(eventId)
                if (event != null) {
                    _uiState.value = _uiState.value.copy(
                        id = event.id,
                        title = event.title,
                        description = event.description,
                        jalaliYear = event.jalaliYear,
                        jalaliMonth = event.jalaliMonth,
                        jalaliDay = event.jalaliDay,
                        hour = event.hour,
                        minute = event.minute,
                        colorHex = event.colorHex,
                        recurrence = event.recurrence,
                        hasReminder = event.hasReminder,
                        reminderMinutesBefore = event.reminderMinutesBefore
                    )
                }
            }
        }
    }

    fun setTitle(title: String) { _uiState.value = _uiState.value.copy(title = title, error = null) }
    fun setDescription(desc: String) { _uiState.value = _uiState.value.copy(description = desc) }
    fun setDate(date: JalaliDate) {
        _uiState.value = _uiState.value.copy(
            jalaliYear = date.year,
            jalaliMonth = date.month,
            jalaliDay = date.day
        )
    }
    fun setTime(hour: Int, minute: Int) { _uiState.value = _uiState.value.copy(hour = hour, minute = minute) }
    fun setColor(colorHex: String) { _uiState.value = _uiState.value.copy(colorHex = colorHex) }
    fun setRecurrence(recurrence: RecurrenceType) { _uiState.value = _uiState.value.copy(recurrence = recurrence) }
    fun setHasReminder(hasReminder: Boolean) { _uiState.value = _uiState.value.copy(hasReminder = hasReminder) }
    fun setReminderMinutesBefore(mins: Int) { _uiState.value = _uiState.value.copy(reminderMinutesBefore = mins) }

    fun saveEvent(onSaved: () -> Unit) {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.value = state.copy(error = "لطفاً عنوان رویداد را وارد کنید")
            return
        }

        viewModelScope.launch {
            val event = Event(
                id = state.id,
                title = state.title.trim(),
                description = state.description.trim(),
                jalaliYear = state.jalaliYear,
                jalaliMonth = state.jalaliMonth,
                jalaliDay = state.jalaliDay,
                hour = state.hour,
                minute = state.minute,
                colorHex = state.colorHex,
                recurrence = state.recurrence,
                hasReminder = state.hasReminder,
                reminderMinutesBefore = state.reminderMinutesBefore
            )

            if (state.id > 0) {
                container.updateEventUseCase(event)
            } else {
                container.addEventUseCase(event)
            }

            _uiState.value = _uiState.value.copy(isSaved = true)
            onSaved()
        }
    }

    fun deleteEvent(onDeleted: () -> Unit) {
        val state = _uiState.value
        if (state.id > 0) {
            viewModelScope.launch {
                container.deleteEventUseCase(state.id)
                onDeleted()
            }
        }
    }
}

// ----------------------------------------------------
// 4. Tasks (To-Do) ViewModel
// ----------------------------------------------------
enum class TaskFilter { ALL, ACTIVE, COMPLETED }

data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val filter: TaskFilter = TaskFilter.ALL,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class TasksViewModel(
    private val container: AppContainer
) : ViewModel() {

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<TasksUiState> = combine(
        container.getAllTasksUseCase(),
        _filter,
        _searchQuery
    ) { tasks, filter, query ->
        val filtered = tasks.filter { task ->
            val matchesFilter = when (filter) {
                TaskFilter.ALL -> true
                TaskFilter.ACTIVE -> !task.isCompleted
                TaskFilter.COMPLETED -> task.isCompleted
            }
            val matchesQuery = query.isBlank() || task.title.contains(query, ignoreCase = true)
            matchesFilter && matchesQuery
        }
        TasksUiState(tasks = filtered, filter = filter, searchQuery = query)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TasksUiState()
    )

    fun setFilter(filter: TaskFilter) { _filter.value = filter }
    fun setSearchQuery(query: String) { _searchQuery.value = query }

    fun addTask(title: String, priority: Priority, date: JalaliDate = JalaliCalendarEngine.getTodayJalali()) {
        if (title.isBlank()) return
        viewModelScope.launch {
            container.addTaskUseCase(
                Task(
                    title = title.trim(),
                    jalaliYear = date.year,
                    jalaliMonth = date.month,
                    jalaliDay = date.day,
                    priority = priority
                )
            )
        }
    }

    fun toggleTask(id: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            container.toggleTaskUseCase(id, isCompleted)
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            container.deleteTaskUseCase(id)
        }
    }
}

// ----------------------------------------------------
// 5. Occasions & Holidays Explorer ViewModel
// ----------------------------------------------------
data class OccasionsUiState(
    val selectedYear: Int = 1403,
    val occasions: List<Occasion> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: OccasionCategory? = null,
    val onlyHolidays: Boolean = false,
    val availableYears: List<Int> = (1394..1406).toList()
)

class OccasionsViewModel(
    private val container: AppContainer
) : ViewModel() {

    private val _selectedYear = MutableStateFlow(JalaliCalendarEngine.getTodayJalali().year)
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<OccasionCategory?>(null)
    private val _onlyHolidays = MutableStateFlow(false)

    val uiState: StateFlow<OccasionsUiState> = combine(
        _selectedYear,
        _searchQuery,
        _selectedCategory,
        _onlyHolidays
    ) { year, query, category, onlyHolidays ->
        Tuple4(year, query, category, onlyHolidays)
    }.flatMapLatest { (year, query, category, onlyHolidays) ->
        container.getOccasionsForYearUseCase(year).map { occasions ->
            val filtered = occasions.filter { occ ->
                val matchesQuery = query.isBlank() || occ.title.contains(query, ignoreCase = true)
                val matchesCategory = category == null || occ.category == category
                val matchesHoliday = !onlyHolidays || occ.isHoliday
                matchesQuery && matchesCategory && matchesHoliday
            }
            OccasionsUiState(
                selectedYear = year,
                occasions = filtered,
                searchQuery = query,
                selectedCategory = category,
                onlyHolidays = onlyHolidays
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OccasionsUiState()
    )

    fun setYear(year: Int) { _selectedYear.value = year }
    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setCategory(category: OccasionCategory?) { _selectedCategory.value = category }
    fun toggleOnlyHolidays() { _onlyHolidays.value = !_onlyHolidays.value }
}

// ----------------------------------------------------
// 6. Settings ViewModel
// ----------------------------------------------------
class SettingsViewModel(
    private val container: AppContainer
) : ViewModel() {

    val settingsState: StateFlow<UserSettings> = container.getSettingsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSettings()
    )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            container.updateSettingsUseCase.setThemeMode(mode)
        }
    }

    fun setPrimaryColorHex(colorHex: String) {
        viewModelScope.launch {
            container.updateSettingsUseCase.setPrimaryColor(colorHex)
        }
    }

    fun setShowGregorianDate(show: Boolean) {
        viewModelScope.launch {
            container.updateSettingsUseCase.setShowGregorianDate(show)
        }
    }

    fun setShowIslamicDate(show: Boolean) {
        viewModelScope.launch {
            container.updateSettingsUseCase.setShowIslamicDate(show)
        }
    }

    fun setEnableNotifications(enable: Boolean) {
        viewModelScope.launch {
            container.updateSettingsUseCase.setEnableNotifications(enable)
        }
    }
}

// Simple Helper Tuple
data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

// Factory Provider
class ViewModelFactory(
    private val container: AppContainer,
    private val extraParam: Any? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CalendarViewModel::class.java) -> CalendarViewModel(container) as T
            modelClass.isAssignableFrom(DayDetailViewModel::class.java) -> DayDetailViewModel(extraParam as JalaliDate, container) as T
            modelClass.isAssignableFrom(EventEditorViewModel::class.java) -> {
                val params = extraParam as? Pair<Long?, JalaliDate?>
                EventEditorViewModel(params?.first, params?.second, container) as T
            }
            modelClass.isAssignableFrom(TasksViewModel::class.java) -> TasksViewModel(container) as T
            modelClass.isAssignableFrom(OccasionsViewModel::class.java) -> OccasionsViewModel(container) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(container) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
