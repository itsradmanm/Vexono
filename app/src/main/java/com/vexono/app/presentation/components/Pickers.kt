package com.vexono.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

@Composable
fun PersianDatePickerDialog(
    initialDate: JalaliDate,
    onDismissRequest: () -> Unit,
    onDateSelected: (JalaliDate) -> Unit
) {
    var selectedYear by remember { mutableIntStateOf(initialDate.year) }
    var selectedMonth by remember { mutableIntStateOf(initialDate.month) }
    var selectedDay by remember { mutableIntStateOf(initialDate.day) }

    val years = (1394..1415).toList()
    val daysInMonth = JalaliCalendarEngine.getDaysInJalaliMonth(selectedYear, selectedMonth)
    if (selectedDay > daysInMonth) {
        selectedDay = daysInMonth
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "انتخاب تاریخ شمسی",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // Header preview
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = JalaliCalendarEngine.getFullPersianDateString(JalaliDate(selectedYear, selectedMonth, selectedDay)),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Year selector
                Text(
                    text = "سال:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(years) { year ->
                        val isSelected = year == selectedYear
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedYear = year }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = JalaliCalendarEngine.toPersianDigits(year),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Month selector
                Text(
                    text = "ماه:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(1..12) { month ->
                        val isSelected = month == selectedMonth
                        val monthName = JalaliCalendarEngine.PERSIAN_MONTH_NAMES[month - 1]
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedMonth = month }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = monthName,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Day selector
                Text(
                    text = "روز:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(1..daysInMonth) { day ->
                        val isSelected = day == selectedDay
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedDay = day }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = JalaliCalendarEngine.toPersianDigits(day),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDateSelected(JalaliDate(selectedYear, selectedMonth, selectedDay))
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("تایید")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismissRequest) {
                Text("انصراف")
            }
        }
    )
}

@Composable
fun PersianTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismissRequest: () -> Unit,
    onTimeSelected: (hour: Int, minute: Int) -> Unit
) {
    var selectedHour by remember { mutableIntStateOf(initialHour) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "انتخاب ساعت",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header preview
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${JalaliCalendarEngine.toPersianDigits(String.format("%02d", selectedHour))}:${JalaliCalendarEngine.toPersianDigits(String.format("%02d", selectedMinute))}",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .padding(14.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hour row
                Text(
                    text = "ساعت (۰ تا ۲۳):",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(0..23) { hour ->
                        val isSelected = hour == selectedHour
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedHour = hour }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = JalaliCalendarEngine.toPersianDigits(String.format("%02d", hour)),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Minute row
                Text(
                    text = "دقیقه:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(4.dp))
                val minutesList = (0..55 step 5).toList()
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(minutesList) { minute ->
                        val isSelected = minute == selectedMinute
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedMinute = minute }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = JalaliCalendarEngine.toPersianDigits(String.format("%02d", minute)),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onTimeSelected(selectedHour, selectedMinute)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("تایید")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismissRequest) {
                Text("انصراف")
            }
        }
    )
}
