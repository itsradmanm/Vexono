package com.vexono.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vexono.app.data.calendar.JalaliCalendarEngine
import com.vexono.app.domain.model.JalaliDate
import com.vexono.app.presentation.theme.LocalCustomColors

// ----------------------------------------------------
// 1. Wheel Scroll Picker for Years (1300 to 1500)
// ----------------------------------------------------

@Composable
fun PersianWheelYearPicker(
    selectedYear: Int,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    startYear: Int = 1300,
    endYear: Int = 1500
) {
    val years = remember(startYear, endYear) { (startYear..endYear).toList() }
    val initialIndex = remember(selectedYear) {
        val idx = years.indexOf(selectedYear)
        if (idx >= 0) idx else (years.size / 2)
    }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (initialIndex - 2).coerceAtLeast(0))
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    var isScrollingInternally by remember { mutableStateOf(false) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                val centerIndex = index + 2
                if (centerIndex in years.indices) {
                    val year = years[centerIndex]
                    if (year != selectedYear) {
                        isScrollingInternally = true
                        onYearSelected(year)
                    }
                }
            }
    }

    LaunchedEffect(selectedYear) {
        if (!isScrollingInternally) {
            val targetIdx = years.indexOf(selectedYear)
            if (targetIdx >= 0) {
                val scrollTarget = (targetIdx - 2).coerceAtLeast(0)
                listState.animateScrollToItem(scrollTarget)
            }
        }
        isScrollingInternally = false
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Center selection bar highlight
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.22f))
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.55f), RoundedCornerShape(10.dp))
        )

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(vertical = 46.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(years) { year ->
                val isSelected = year == selectedYear
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .clickable { onYearSelected(year) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = JalaliCalendarEngine.toPersianDigits(year),
                        color = if (isSelected) Color.White else LocalCustomColors.current.textMuted.copy(alpha = 0.7f),
                        fontSize = if (isSelected) 18.sp else 14.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Top & Bottom gradient fade mask
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(LocalCustomColors.current.surfaceElevated, Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, LocalCustomColors.current.surfaceElevated)
                    )
                )
        )
    }
}

// ----------------------------------------------------
// 2. Glassy Persian Date Picker Dialog (1300 to 1500)
// ----------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersianDatePickerDialog(
    initialDate: JalaliDate,
    onDismissRequest: () -> Unit,
    onDateSelected: (JalaliDate) -> Unit
) {
    var selectedYear by remember { mutableIntStateOf(initialDate.year) }
    var selectedMonth by remember { mutableIntStateOf(initialDate.month) }
    var selectedDay by remember { mutableIntStateOf(initialDate.day) }

    val daysInMonth = JalaliCalendarEngine.getDaysInJalaliMonth(selectedYear, selectedMonth)
    if (selectedDay > daysInMonth) {
        selectedDay = daysInMonth
    }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest
    ) {
        val customColors = LocalCustomColors.current
        GlassSurface(
            shape = RoundedCornerShape(24.dp),
            backgroundColor = customColors.surfaceElevated.copy(alpha = 0.95f),
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Dialog Title
                Text(
                    text = "انتخاب تاریخ هجری شمسی",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Date Preview Card (Glassy)
                GlassCard(
                    backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = JalaliCalendarEngine.getFullPersianDateString(JalaliDate(selectedYear, selectedMonth, selectedDay)),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Year Selector: Dynamic Wheel Picker (1300 to 1500)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "سال شمسی (۱۳۰۰ تا ۱۵۰۰):",
                        style = MaterialTheme.typography.labelMedium,
                        color = LocalCustomColors.current.textMuted
                    )
                    Text(
                        text = JalaliCalendarEngine.toPersianDigits(selectedYear),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                PersianWheelYearPicker(
                    selectedYear = selectedYear,
                    onYearSelected = { selectedYear = it },
                    startYear = 1300,
                    endYear = 1500
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Month Selector
                Text(
                    text = "ماه:",
                    style = MaterialTheme.typography.labelMedium,
                    color = LocalCustomColors.current.textMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth().height(140.dp)
                ) {
                    items((1..12).toList()) { month ->
                        val isSelected = month == selectedMonth
                        val monthName = JalaliCalendarEngine.PERSIAN_MONTH_NAMES[month - 1]
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                    else Color.White.copy(alpha = 0.06f)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedMonth = month }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = monthName,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Day Selector
                Text(
                    text = "روز:",
                    style = MaterialTheme.typography.labelMedium,
                    color = LocalCustomColors.current.textMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth().height(180.dp)
                ) {
                    items((1..daysInMonth).toList()) { day ->
                        val isSelected = day == selectedDay
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                    else Color.White.copy(alpha = 0.06f)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedDay = day }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = JalaliCalendarEngine.toPersianDigits(day),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Dialog Buttons (Glassy)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassButton(
                        onClick = {
                            onDateSelected(JalaliDate(selectedYear, selectedMonth, selectedDay))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("تایید", fontWeight = FontWeight.Bold)
                    }

                    GlassOutlinedButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("انصراف", fontWeight = FontWeight.Normal)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 3. Glassy Persian Time Picker Dialog
// ----------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersianTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismissRequest: () -> Unit,
    onTimeSelected: (hour: Int, minute: Int) -> Unit
) {
    var selectedHour by remember { mutableIntStateOf(initialHour) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest
    ) {
        val customColors = LocalCustomColors.current
        GlassSurface(
            shape = RoundedCornerShape(24.dp),
            backgroundColor = customColors.surfaceElevated.copy(alpha = 0.95f),
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "انتخاب ساعت و دقیقه",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Time Preview (Glassy)
                GlassCard(
                    backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${JalaliCalendarEngine.toPersianDigits(String.format("%02d", selectedHour))}:${JalaliCalendarEngine.toPersianDigits(String.format("%02d", selectedMinute))}",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hour row
                Text(
                    text = "ساعت (۰ تا ۲۳):",
                    style = MaterialTheme.typography.labelMedium,
                    color = LocalCustomColors.current.textMuted,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items((0..23).toList()) { hour ->
                        val isSelected = hour == selectedHour
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                    else Color.White.copy(alpha = 0.06f)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedHour = hour }
                                .padding(horizontal = 11.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = JalaliCalendarEngine.toPersianDigits(String.format("%02d", hour)),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
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
                    color = LocalCustomColors.current.textMuted,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))
                val minutesList = (0..55 step 5).toList()
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(minutesList) { minute ->
                        val isSelected = minute == selectedMinute
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                    else Color.White.copy(alpha = 0.06f)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedMinute = minute }
                                .padding(horizontal = 11.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = JalaliCalendarEngine.toPersianDigits(String.format("%02d", minute)),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Dialog Buttons (Glassy)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassButton(
                        onClick = {
                            onTimeSelected(selectedHour, selectedMinute)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("تایید", fontWeight = FontWeight.Bold)
                    }

                    GlassOutlinedButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("انصراف", fontWeight = FontWeight.Normal)
                    }
                }
            }
        }
    }
}
