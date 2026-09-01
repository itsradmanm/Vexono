package com.vexono.app.presentation.screens.event_editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vexono.app.data.calendar.JalaliCalendarEngine
import com.vexono.app.domain.model.JalaliDate
import com.vexono.app.domain.model.RecurrenceType
import com.vexono.app.presentation.components.PersianDatePickerDialog
import com.vexono.app.presentation.components.PersianTimePickerDialog
import com.vexono.app.presentation.theme.LocalCustomColors
import com.vexono.app.presentation.viewmodel.EventEditorViewModel

@Composable
fun EventEditorScreen(
    viewModel: EventEditorViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val customColors = LocalCustomColors.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val colorOptions = listOf(
        "#7C4DFF", "#00E5C7", "#10B981",
        "#FFB300", "#FF5C7A", "#3B82F6"
    )

    val reminderOptions = listOf(
        0 to "در زمان رویداد",
        5 to "۵ دقیقه قبل",
        15 to "۱۵ دقیقه قبل",
        30 to "۳۰ دقیقه قبل",
        60 to "۱ ساعت قبل",
        1440 to "۱ روز قبل"
    )

    val recurrenceOptions = listOf(
        RecurrenceType.NONE to "یک‌باره",
        RecurrenceType.DAILY to "روزانه",
        RecurrenceType.WEEKLY to "هفتگی",
        RecurrenceType.MONTHLY to "ماهانه",
        RecurrenceType.YEARLY to "سالانه"
    )

    if (showDatePicker) {
        PersianDatePickerDialog(
            initialDate = JalaliDate(uiState.jalaliYear, uiState.jalaliMonth, uiState.jalaliDay),
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { date ->
                viewModel.setDate(date)
                showDatePicker = false
            }
        )
    }

    if (showTimePicker) {
        PersianTimePickerDialog(
            initialHour = uiState.hour,
            initialMinute = uiState.minute,
            onDismissRequest = { showTimePicker = false },
            onTimeSelected = { hour, minute ->
                viewModel.setTime(hour, minute)
                showTimePicker = false
            }
        )
    }

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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "بازگشت",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (uiState.id > 0) "ویرایش رویداد" else "افزودن رویداد جدید",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (uiState.id > 0) {
                        IconButton(onClick = { viewModel.deleteEvent(onNavigateBack) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "حذف رویداد",
                                tint = customColors.holidayColor
                            )
                        }
                    }
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
                        onClick = { viewModel.saveEvent(onNavigateBack) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (uiState.id > 0) "ذخیره تغییرات" else "ثبت رویداد",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error banner if any
            if (uiState.error != null) {
                Surface(
                    color = customColors.holidayColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.error ?: "",
                        color = customColors.holidayColor,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // 1. Title Input
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.setTitle(it) },
                label = { Text("عنوان رویداد *") },
                placeholder = { Text("مثال: جلسه کاری، تولد علی...") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // 2. Description Input
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.setDescription(it) },
                label = { Text("توضیحات و یادداشت (اختیاری)") },
                placeholder = { Text("توضیحات بیشتر در مورد این رویداد...") },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // 3. Date & Time Selectors Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date Picker Button
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showDatePicker = true }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("تاریخ شمسی", fontSize = 11.sp, color = customColors.textMuted)
                            Text(
                                text = "${JalaliCalendarEngine.toPersianDigits(uiState.jalaliDay)} ${JalaliCalendarEngine.PERSIAN_MONTH_NAMES.getOrElse(uiState.jalaliMonth - 1) { "" }} ${JalaliCalendarEngine.toPersianDigits(uiState.jalaliYear)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Time Picker Button
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showTimePicker = true }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = customColors.accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("ساعت", fontSize = 11.sp, color = customColors.textMuted)
                            Text(
                                text = "${JalaliCalendarEngine.toPersianDigits(String.format("%02d", uiState.hour))}:${JalaliCalendarEngine.toPersianDigits(String.format("%02d", uiState.minute))}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // 4. Color Tag Palette
            Column {
                Text(
                    text = "رنگ برچسب رویداد:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    colorOptions.forEach { hex ->
                        val itemColor = Color(android.graphics.Color.parseColor(hex))
                        val isSelected = uiState.colorHex.equals(hex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(itemColor)
                                .clickable { viewModel.setColor(hex) }
                                .then(
                                    if (isSelected) Modifier.border(2.5.dp, Color.White, CircleShape)
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 5. Recurrence Selector
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "تکرار رویداد:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(recurrenceOptions) { (type, label) ->
                        val isSelected = uiState.recurrence == type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { viewModel.setRecurrence(type) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // 6. Reminder & Notification
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "یادآور و نوتیفیکیشن",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "ارسال اعلان در موعد تعیین شده",
                                    fontSize = 12.sp,
                                    color = customColors.textMuted
                                )
                            }
                        }

                        Switch(
                            checked = uiState.hasReminder,
                            onCheckedChange = { viewModel.setHasReminder(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    if (uiState.hasReminder) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "زمان ارسال هشدار:",
                            fontSize = 12.sp,
                            color = customColors.textMuted
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(reminderOptions) { (mins, label) ->
                                val isSelected = uiState.reminderMinutesBefore == mins
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) customColors.accentColor else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { viewModel.setReminderMinutesBefore(mins) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
