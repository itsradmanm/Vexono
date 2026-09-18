package com.vexono.app.presentation.screens.calendar

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.vexono.app.presentation.components.GlassButton
import com.vexono.app.presentation.components.GlassCard
import com.vexono.app.presentation.components.GlassFloatingActionButton
import com.vexono.app.presentation.components.GlassIconButton
import com.vexono.app.presentation.components.GlassOutlinedButton
import com.vexono.app.presentation.components.GlassSurface
import com.vexono.app.presentation.components.OccasionCategoryBadge
import com.vexono.app.presentation.components.OccasionDetailBottomSheet
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
    // FIX: BottomSheet is only shown when explicitly triggered, NOT on every day click
    var activeBottomSheetDay by remember { mutableStateOf<CalendarDay?>(null) }

    val monthName = JalaliCalendarEngine.PERSIAN_MONTH_NAMES.getOrElse(uiState.currentMonth - 1) { "" }
    val yearString = JalaliCalendarEngine.toPersianDigits(uiState.currentYear)

    // Horizontal Swipe detection for month change
    var dragAccumulator by remember { mutableStateOf(0f) }

    // BackHandler: close FAB menu or BottomSheet before navigating back
    BackHandler(enabled = isFabExpanded || activeBottomSheetDay != null) {
        when {
            isFabExpanded -> isFabExpanded = false
            activeBottomSheetDay != null -> activeBottomSheetDay = null
        }
    }

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

    activeBottomSheetDay?.let { day ->
        OccasionDetailBottomSheet(
            day = day,
            onDismissRequest = { activeBottomSheetDay = null },
            onViewDayDetail = {
                activeBottomSheetDay = null
                onDayDetailRequested(day.jalaliDate)
            },
            onAddEvent = {
                activeBottomSheetDay = null
                onAddEventRequested(day.jalaliDate)
            },
            onAddTask = {
                activeBottomSheetDay = null
                onAddTaskRequested(day.jalaliDate)
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
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
                // 1. Glass Top Header Bar
                GlassSurface(
                    shape = RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp),
                    backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Month & Year Selector Trigger (Glassy Pill)
                        GlassCard(
                            backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            onClick = { showDatePickerDialog = true }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "$monthName $yearString",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "انتخاب ماه",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Navigation Actions (Prev, Next, Today)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            GlassIconButton(
                                onClick = { viewModel.prevMonth() },
                                size = 36.dp
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForwardIos, // RTL: forward is previous
                                    contentDescription = "ماه قبل",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Today Button (Glassy)
                            GlassButton(
                                onClick = { viewModel.jumpToToday() },
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "امروز",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            GlassIconButton(
                                onClick = { viewModel.nextMonth() },
                                size = 36.dp
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBackIosNew, // RTL: back is next
                                    contentDescription = "ماه بعد",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Weekday Header Row (شنبه تا جمعه)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    JalaliCalendarEngine.PERSIAN_WEEKDAY_NAMES_SHORT.forEachIndexed { index, name ->
                        val isFriday = (index == 6)
                        Text(
                            text = name,
                            color = if (isFriday) customColors.holidayColor else MaterialTheme.colorScheme.onSurfaceVariant,
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
                                // FIX: Only select the day - do NOT open BottomSheet automatically
                                viewModel.selectDay(day)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 4. Selected Day Preview Card (Glassy) - clicking opens BottomSheet
                uiState.selectedDay?.let { selected ->
                    GlassCard(
                        shape = RoundedCornerShape(22.dp),
                        backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        // Clicking the preview card opens the BottomSheet
                        onClick = { activeBottomSheetDay = selected }
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
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontSize = 11.sp
                                                )
                                            }
                                            if (uiState.userSettings.showGregorianDate && uiState.userSettings.showIslamicDate) {
                                                Text(
                                                    text = "  •  ",
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontSize = 11.sp
                                                )
                                            }
                                            if (uiState.userSettings.showIslamicDate) {
                                                Text(
                                                    text = JalaliCalendarEngine.getFullIslamicDateString(selected.islamicDate),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                // View Details button - directly navigates to day detail
                                GlassOutlinedButton(
                                    onClick = { onDayDetailRequested(selected.jalaliDate) },
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("جزئیات روز", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                                                fontWeight = if (occ.isHoliday) FontWeight.Bold else FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }

                            // Events and Tasks count preview
                            if (selected.eventCount > 0 || selected.taskCount > 0) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
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

            // FAB Scrim: semi-transparent overlay when FAB menu is open
            AnimatedVisibility(
                visible = isFabExpanded,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { isFabExpanded = false }
                )
            }

            // FAB Column (positioned at bottom-end)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 18.dp, bottom = 18.dp)
                    .navigationBarsPadding()
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
                        // Add Task Action (Theme Adaptive)
                        GlassCard(
                            backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                            borderColor = customColors.accentColor.copy(alpha = 0.35f),
                            onClick = {
                                isFabExpanded = false
                                val targetDate = uiState.selectedDay?.jalaliDate ?: JalaliCalendarEngine.getTodayJalali()
                                onAddTaskRequested(targetDate)
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "افزودن تسک",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Checklist,
                                    contentDescription = null,
                                    tint = customColors.accentColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Add Event Action (Theme Adaptive)
                        GlassCard(
                            backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                            onClick = {
                                isFabExpanded = false
                                val targetDate = uiState.selectedDay?.jalaliDate ?: JalaliCalendarEngine.getTodayJalali()
                                onAddEventRequested(targetDate)
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "افزودن رویداد",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Glass Floating Action Button
                GlassFloatingActionButton(
                    onClick = { isFabExpanded = !isFabExpanded }
                ) {
                    Icon(
                        imageVector = if (isFabExpanded) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "منوی افزودن",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
