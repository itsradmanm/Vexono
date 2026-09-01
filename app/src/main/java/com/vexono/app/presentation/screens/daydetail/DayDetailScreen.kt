package com.vexono.app.presentation.screens.daydetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vexono.app.data.calendar.JalaliCalendarEngine
import com.vexono.app.domain.model.Event
import com.vexono.app.domain.model.JalaliDate
import com.vexono.app.domain.model.Task
import com.vexono.app.presentation.components.EmptyStateView
import com.vexono.app.presentation.components.OccasionCategoryBadge
import com.vexono.app.presentation.components.PriorityBadge
import com.vexono.app.presentation.theme.LocalCustomColors
import com.vexono.app.presentation.viewmodel.DayDetailViewModel

@Composable
fun DayDetailScreen(
    viewModel: DayDetailViewModel,
    onNavigateBack: () -> Unit,
    onAddEventRequested: (JalaliDate) -> Unit,
    onEditEventRequested: (Long) -> Unit,
    onAddTaskRequested: (JalaliDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val customColors = LocalCustomColors.current

    val gregorianDate = JalaliCalendarEngine.jalaliToGregorian(uiState.jalaliDate)
    val islamicDate = JalaliCalendarEngine.jalaliToIslamic(uiState.jalaliDate)

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "بازگشت",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "جزئیات روز",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onAddEventRequested(uiState.jalaliDate) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("افزودن رویداد", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onAddTaskRequested(uiState.jalaliDate) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("افزودن تسک", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // 1. Full Date Banner Card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = JalaliCalendarEngine.getFullPersianDateString(uiState.jalaliDate),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "میلادی: ${JalaliCalendarEngine.getFullGregorianDateString(gregorianDate)}",
                                color = customColors.textMuted,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "  •  ",
                                color = customColors.textMuted,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "قمری: ${JalaliCalendarEngine.getFullIslamicDateString(islamicDate)}",
                                color = customColors.textMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // 2. Occasions Section
            if (uiState.occasions.isNotEmpty()) {
                item {
                    Text(
                        text = "مناسبت‌های امروز",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.occasions.forEach { occ ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OccasionCategoryBadge(
                                        category = occ.category,
                                        isHoliday = occ.isHoliday
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = occ.title,
                                        color = if (occ.isHoliday) customColors.holidayColor else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (occ.isHoliday) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Events Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "رویدادها (${JalaliCalendarEngine.toPersianDigits(uiState.events.size)})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            if (uiState.events.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.Event,
                        title = "رویدادی برای این روز ثبت نشده",
                        description = "برای ثبت جلسه، قرار کاری یا یادآور روزانه دکمه افزودن رویداد را بزنید.",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                items(uiState.events) { event ->
                    EventItemCard(
                        event = event,
                        onEdit = { onEditEventRequested(event.id) },
                        onDelete = { viewModel.deleteEvent(event.id) }
                    )
                }
            }

            // 4. Tasks Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تسک‌ها (${JalaliCalendarEngine.toPersianDigits(uiState.tasks.size)})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            if (uiState.tasks.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.Checklist,
                        title = "تسکی برای این روز تعریف نشده",
                        description = "لیست کارهای روزانه و چک‌لیست خود را اینجا اضافه کنید.",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                items(uiState.tasks) { task ->
                    TaskItemCard(
                        task = task,
                        onToggle = { isChecked -> viewModel.toggleTask(task.id, isChecked) },
                        onDelete = { viewModel.deleteTask(task.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun EventItemCard(
    event: Event,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val eventColor = runCatching { Color(android.graphics.Color.parseColor(event.colorHex)) }.getOrDefault(MaterialTheme.colorScheme.primary)

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color strip
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 44.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(eventColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (event.description.isNotBlank()) {
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = LocalCustomColors.current.textMuted,
                        maxLines = 2
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = eventColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${JalaliCalendarEngine.toPersianDigits(String.format("%02d", event.hour))}:${JalaliCalendarEngine.toPersianDigits(String.format("%02d", event.minute))}",
                        fontSize = 12.sp,
                        color = eventColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "ویرایش",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "حذف",
                    tint = LocalCustomColors.current.holidayColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun TaskItemCard(
    task: Task,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onToggle,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Medium,
                    color = if (task.isCompleted) LocalCustomColors.current.textMuted else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
            }

            PriorityBadge(priority = task.priority)

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "حذف",
                    tint = LocalCustomColors.current.holidayColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
