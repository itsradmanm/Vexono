package com.vexono.app.presentation.screens.tasks

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vexono.app.data.calendar.JalaliCalendarEngine
import com.vexono.app.domain.model.JalaliDate
import com.vexono.app.domain.model.Priority
import com.vexono.app.domain.model.Task
import com.vexono.app.presentation.components.EmptyStateView
import com.vexono.app.presentation.components.PersianDatePickerDialog
import com.vexono.app.presentation.components.PriorityBadge
import com.vexono.app.presentation.theme.LocalCustomColors
import com.vexono.app.presentation.viewmodel.TaskFilter
import com.vexono.app.presentation.viewmodel.TasksViewModel

@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val customColors = LocalCustomColors.current

    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var newTaskDate by remember { mutableStateOf(JalaliCalendarEngine.getTodayJalali()) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        PersianDatePickerDialog(
            initialDate = newTaskDate,
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { date ->
                newTaskDate = date
                showDatePicker = false
            }
        )
    }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "تسک‌های روزانه (To-Do)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search input
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("جستجوی تسک‌ها...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = customColors.textMuted)
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "پاک کردن", tint = customColors.textMuted)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Filter tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterTab(
                            title = "همه",
                            isSelected = uiState.filter == TaskFilter.ALL,
                            onClick = { viewModel.setFilter(TaskFilter.ALL) }
                        )
                        FilterTab(
                            title = "در دست انجام",
                            isSelected = uiState.filter == TaskFilter.ACTIVE,
                            onClick = { viewModel.setFilter(TaskFilter.ACTIVE) }
                        )
                        FilterTab(
                            title = "تکمیل‌شده",
                            isSelected = uiState.filter == TaskFilter.COMPLETED,
                            onClick = { viewModel.setFilter(TaskFilter.COMPLETED) }
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Fast Add Task Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newTaskTitle,
                            onValueChange = { newTaskTitle = it },
                            placeholder = { Text("تسک جدید را بنویسید...") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                if (newTaskTitle.isNotBlank()) {
                                    viewModel.addTask(newTaskTitle, newTaskPriority, newTaskDate)
                                    newTaskTitle = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "افزودن تسک")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Priority and Date Selector Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Date selector chip
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { showDatePicker = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = customColors.accentColor, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${JalaliCalendarEngine.toPersianDigits(newTaskDate.day)} ${JalaliCalendarEngine.PERSIAN_MONTH_NAMES.getOrElse(newTaskDate.month - 1) { "" }}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Priority chips
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(
                                Priority.LOW to "پایین",
                                Priority.MEDIUM to "متوسط",
                                Priority.HIGH to "بالا"
                            ).forEach { (p, label) ->
                                val isSelected = newTaskPriority == p
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { newTaskPriority = p }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        if (uiState.tasks.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.Checklist,
                title = "هیچ تسکی یافت نشد",
                description = "کارهای روزمره خود را از کادر پایین اضافه کنید تا برنامه‌ریزی منظمی داشته باشید.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.tasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onToggle = { isChecked -> viewModel.toggleTask(task.id, isChecked) },
                        onDelete = { viewModel.deleteTask(task.id) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun FilterTab(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TaskCard(
    task: Task,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val customColors = LocalCustomColors.current
    val taskDate = JalaliDate(task.jalaliYear, task.jalaliMonth, task.jalaliDay)

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 12.dp)
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

            Spacer(modifier = Modifier.width(4.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Medium,
                    color = if (task.isCompleted) customColors.textMuted else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${JalaliCalendarEngine.toPersianDigits(task.jalaliDay)} ${JalaliCalendarEngine.PERSIAN_MONTH_NAMES.getOrElse(task.jalaliMonth - 1) { "" }} ${JalaliCalendarEngine.toPersianDigits(task.jalaliYear)}",
                    fontSize = 11.sp,
                    color = customColors.textMuted
                )
            }

            PriorityBadge(priority = task.priority)

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "حذف تسک",
                    tint = customColors.holidayColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
