package com.vexono.app.presentation.screens.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vexono.app.data.calendar.JalaliCalendarEngine
import com.vexono.app.domain.model.CalendarDay
import com.vexono.app.domain.model.JalaliDate
import com.vexono.app.presentation.components.CalendarDayCell
import com.vexono.app.presentation.components.OccasionCategoryBadge
import com.vexono.app.presentation.components.PersianDatePickerDialog
import com.vexono.app.presentation.theme.LocalCustomColors
import com.vexono.app.presentation.viewmodel.CalendarViewModel

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onDayDetailRequested: (JalaliDate) -> Unit,
    onAddEventRequested: (JalaliDate) -> Unit,
    onAddTaskRequested: (JalaliDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val customColors = LocalCustomColors.current

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var isFabExpanded by remember { mutableStateOf(false) }

    val monthName = JalaliCalendarEngine.PERSIAN_MONTH_NAMES.getOrElse(uiState.currentMonth - 1) { "" }
    val yearString = JalaliCalendarEngine.toPersianDigits(uiState.currentYear)

    // Horizontal Swipe detection for month change
    var dragAccumulator by remember { mutableStateOf(0f) }

    if (showDatePickerDialog) {
        PersianDatePickerDialog(
            initialDate = JalaliDate(uiState.currentYear, uiState.currentMonth, 1),
            onDismissRequest = { showDatePickerDialog = false },
            onDateSelected = { selectedDate ->
                viewModel.setYearMonth(selectedDate.year, selectedDate.month)
                showDatePickerDialog = false
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnimatedVisibility(
                    visible = isFabExpanded,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it }
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Add Task Action
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    isFabExpanded = false
                                    val targetDate = uiState.selectedDay?.jalaliDate ?: JalaliCalendarEngine.getTodayJalali()
                                    onAddTaskRequested(targetDate)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "افزودن تسک",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Checklist,
                                contentDescription = null,
                                tint = customColors.accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Add Event Action
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    isFabExpanded = false
                                    val targetDate = uiState.selectedDay?.jalaliDate ?: JalaliCalendarEngine.getTodayJalali()
                                    onAddEventRequested(targetDate)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "افزودن رویداد",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { isFabExpanded = !isFabExpanded },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (isFabExpanded) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (dragAccumulator > 70) {
                                viewModel.prevMonth()
                            } else if (dragAccumulator < -70) {
                                viewModel.nextMonth()
                            }
                            dragAccumulator = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            dragAccumulator += dragAmount
                        }
                    )
                }
        ) {
            // 1. Top Header Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Month & Year Selector Trigger
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showDatePickerDialog = true }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$monthName $yearString",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select month",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Navigation Actions (Prev, Next, Today)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.prevMonth() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos, // RTL: forward is previous
                                contentDescription = "Previous Month",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Today Button
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.jumpToToday() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "امروز",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.nextMonth() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew, // RTL: back is next
                                contentDescription = "Next Month",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // 2. Weekday Header Row (شنبه تا جمعه)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                JalaliCalendarEngine.PERSIAN_WEEKDAY_NAMES_SHORT.forEachIndexed { index, name ->
                    val isFriday = (index == 6)
                    Text(
                        text = name,
                        color = if (isFriday) customColors.holidayColor else customColors.textMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 3. Calendar Grid (7 columns)
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
                    .animateContentSize(tween(250))
            ) {
                items(uiState.days) { day ->
                    val isSelected = uiState.selectedDay?.jalaliDate == day.jalaliDate
                    CalendarDayCell(
                        day = day,
                        isSelected = isSelected,
                        showGregorian = uiState.userSettings.showGregorianDate,
                        onClick = {
                            viewModel.selectDay(day)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4. Selected Day Preview Card
            uiState.selectedDay?.let { selected ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .clickable { onDayDetailRequested(selected.jalaliDate) }
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        // Date Header Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = JalaliCalendarEngine.getFullPersianDateString(selected.jalaliDate),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected.isHoliday) customColors.holidayColor else MaterialTheme.colorScheme.onSurface
                                )
                                if (uiState.userSettings.showGregorianDate || uiState.userSettings.showIslamicDate) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        if (uiState.userSettings.showGregorianDate) {
                                            Text(
                                                text = JalaliCalendarEngine.getFullGregorianDateString(selected.gregorianDate),
                                                color = customColors.textMuted,
                                                fontSize = 12.sp
                                            )
                                        }
                                        if (uiState.userSettings.showGregorianDate && uiState.userSettings.showIslamicDate) {
                                            Text(
                                                text = "  •  ",
                                                color = customColors.textMuted,
                                                fontSize = 12.sp
                                            )
                                        }
                                        if (uiState.userSettings.showIslamicDate) {
                                            Text(
                                                text = JalaliCalendarEngine.getFullIslamicDateString(selected.islamicDate),
                                                color = customColors.textMuted,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }

                            // View Details button
                            OutlinedButton(
                                onClick = { onDayDetailRequested(selected.jalaliDate) },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("جزئیات", fontSize = 12.sp)
                            }
                        }

                        // Occasions list preview
                        if (selected.occasions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                selected.occasions.forEach { occ ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        OccasionCategoryBadge(
                                            category = occ.category,
                                            isHoliday = occ.isHoliday
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = occ.title,
                                            color = if (occ.isHoliday) customColors.holidayColor else MaterialTheme.colorScheme.onSurface,
                                            fontSize = 12.sp,
                                            fontWeight = if (occ.isHoliday) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        // Events and Tasks count preview
                        if (selected.eventCount > 0 || selected.taskCount > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (selected.eventCount > 0) {
                                    Text(
                                        text = "📅 ${JalaliCalendarEngine.toPersianDigits(selected.eventCount)} رویداد",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                if (selected.taskCount > 0) {
                                    Text(
                                        text = "✅ ${JalaliCalendarEngine.toPersianDigits(selected.completedTaskCount)} از ${JalaliCalendarEngine.toPersianDigits(selected.taskCount)} تسک",
                                        color = customColors.accentColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
