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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.vexono.app.presentation.components.GlassButton
import com.vexono.app.presentation.components.GlassCard
import com.vexono.app.presentation.components.GlassIconButton
import com.vexono.app.presentation.components.GlassOutlinedButton
import com.vexono.app.presentation.components.GlassSurface
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
            GlassSurface(
                shape = RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp),
                backgroundColor = Color.White.copy(alpha = 0.05f),
                borderColor = Color.White.copy(alpha = 0.1f),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassIconButton(
                        onClick = onNavigateBack,
                        size = 38.dp
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "بازگشت",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "جزئیات و برنامه‌های روز",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        bottomBar = {
            GlassSurface(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                backgroundColor = customColors.bottomBarBackground.copy(alpha = 0.96f),
                borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                shadowElevation = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlassButton(
                        onClick = { onAddEventRequested(uiState.jalaliDate) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("افزودن رویداد", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    GlassOutlinedButton(
                        onClick = { onAddTaskRequested(uiState.jalaliDate) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("افزودن تسک", fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // 1. Full Date Banner Card (Glassy)
                GlassCard(
                    shape = RoundedCornerShape(22.dp),
                    backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
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
                                fontSize = 12.sp
                            )
                            Text(
                                text = "  •  ",
                                color = customColors.textMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "قمری: ${JalaliCalendarEngine.getFullIslamicDateString(islamicDate)}",
                                color = customColors.textMuted,
                                fontSize = 12.sp
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
                            GlassCard(
                                shape = RoundedCornerShape(16.dp),
                                backgroundColor = if (occ.isHoliday) customColors.holidayColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                borderColor = if (occ.isHoliday) customColors.holidayColor.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
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
                    GlassEventItemCard(
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
                    GlassTaskItemCard(
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
private fun GlassEventItemCard(
    event: Event,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val eventColor = runCatching { Color(android.graphics.Color.parseColor(event.colorHex)) }.getOrDefault(MaterialTheme.colorScheme.primary)

    GlassCard(
        shape = RoundedCornerShape(16.dp),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
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

            GlassIconButton(
                onClick = onEdit,
                size = 36.dp
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "ویرایش",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            GlassIconButton(
                onClick = onDelete,
                size = 36.dp,
                containerColor = LocalCustomColors.current.holidayColor.copy(alpha = 0.1f),
                borderColor = LocalCustomColors.current.holidayColor.copy(alpha = 0.25f)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "حذف",
                    tint = LocalCustomColors.current.holidayColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun GlassTaskItemCard(
    task: Task,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(
        shape = RoundedCornerShape(16.dp),
        backgroundColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        borderColor = if (task.isCompleted) MaterialTheme.colorScheme.outline.copy(alpha = 0.1f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
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
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
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

            Spacer(modifier = Modifier.width(6.dp))

            GlassIconButton(
                onClick = onDelete,
                size = 36.dp,
                containerColor = LocalCustomColors.current.holidayColor.copy(alpha = 0.1f),
                borderColor = LocalCustomColors.current.holidayColor.copy(alpha = 0.25f)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "حذف",
                    tint = LocalCustomColors.current.holidayColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
