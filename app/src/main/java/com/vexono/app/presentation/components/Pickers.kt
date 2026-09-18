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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

// ----------------------------------------------------
// 1. Wheel Scroll Picker for Years (1300 to 1500)
//    FIX: snapshotFlow for reactive scroll sync
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

    // We show 5 items at a time; the center (index 2) is the selected item.
    // contentPadding = 2 * itemHeight so the first and last items can center.
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (initialIndex - 2).coerceAtLeast(0)
    )
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // BUGFIX: React to scroll position in real-time using snapshotFlow
    // The center item index = firstVisibleItemIndex + 2 (since padding shows 2 items above)
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { firstVisible ->
                // Center of the visible 5 items = firstVisible + 2
                (firstVisible + 2).coerceIn(years.indices)
            }
            .distinctUntilChanged()
            .collect { centeredIndex ->
                val centeredYear = years.getOrNull(centeredIndex)
                if (centeredYear != null && centeredYear != selectedYear) {
                    onYearSelected(centeredYear)
                }
            }
    }

    // Scroll to selectedYear when it changes externally (e.g. from dialog)
    LaunchedEffect(selectedYear) {
        val targetIdx = years.indexOf(selectedYear)
        if (targetIdx >= 0) {
            val scrollTarget = (targetIdx - 2).coerceAtLeast(0)
            if (listState.firstVisibleItemIndex != scrollTarget) {
                listState.animateScrollToItem(scrollTarget)
            }
        }
    }

    val customColors = LocalCustomColors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
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
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                        fontSize = if (isSelected) 18.sp else 14.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Top & Bottom gradient fade mask - color matches theme background (NOT hardcoded black)
        val maskColor = customColors.wheelPickerMaskColor
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(maskColor.copy(alpha = 0.95f), Color.Transparent)
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
                        colors = listOf(Color.Transparent, maskColor.copy(alpha = 0.95f))
                    )
                )
        )
    }
}

// ----------------------------------------------------
// 2. Glassy Persian Date Picker Dialog (1300 to 1500)
//    FIX: Dynamic background, Month 3x4 grid, Day 5x7 grid
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

    val customColors = LocalCustomColors.current

    BasicAlertDialog(
        onDismissRequest = onDismissRequest
    ) {
        GlassSurface(
            shape = RoundedCornerShape(24.dp),
            backgroundColor = customColors.dialogBackground,
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
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
                        text = "سال شمسی:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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

                // 2. Month Selector: 3×4 Grid (all 12 months visible at once)
                Text(
                    text = "ماه:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(164.dp), // 4 rows × ~38dp + spacing
                    userScrollEnabled = false
                ) {
                    itemsIndexed((1..12).toList()) { _, month ->
                        val isSelected = month == selectedMonth
                        val monthName = JalaliCalendarEngine.PERSIAN_MONTH_NAMES[month - 1]
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedMonth = month },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = monthName,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Day Selector: 7×5 grid (like a mini calendar)
                Text(
                    text = "روز:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                val dayRows = (daysInMonth / 7) + if (daysInMonth % 7 != 0) 1 else 0
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((dayRows * 36 + (dayRows - 1) * 4).dp),
                    userScrollEnabled = false
                ) {
                    itemsIndexed((1..daysInMonth).toList()) { _, day ->
                        val isSelected = day == selectedDay
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedDay = day },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = JalaliCalendarEngine.toPersianDigits(day),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center
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
// 3. Glassy Persian Time Picker Dialog (Theme Adaptive)
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

    val customColors = LocalCustomColors.current

    BasicAlertDialog(
        onDismissRequest = onDismissRequest
    ) {
        GlassSurface(
            shape = RoundedCornerShape(24.dp),
            backgroundColor = customColors.dialogBackground,
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
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

                // Hour grid (0-23) as 4×6 grid
                Text(
                    text = "ساعت (۰ تا ۲۳):",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp), // 4 rows × 20dp + spacing
                    userScrollEnabled = false
                ) {
                    itemsIndexed((0..23).toList()) { _, hour ->
                        val isSelected = hour == selectedHour
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedHour = hour },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = JalaliCalendarEngine.toPersianDigits(String.format("%02d", hour)),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Minute row (0, 5, 10, ... 55)
                Text(
                    text = "دقیقه:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))
                val minutesList = (0..55 step 5).toList()
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp), // 2 rows
                    userScrollEnabled = false
                ) {
                    itemsIndexed(minutesList) { _, minute ->
                        val isSelected = minute == selectedMinute
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedMinute = minute },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = JalaliCalendarEngine.toPersianDigits(String.format("%02d", minute)),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center
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
