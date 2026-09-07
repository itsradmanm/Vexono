package com.vexono.app.presentation.screens.occasions

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vexono.app.data.calendar.JalaliCalendarEngine
import com.vexono.app.domain.model.Occasion
import com.vexono.app.domain.model.OccasionCategory
import com.vexono.app.presentation.components.EmptyStateView
import com.vexono.app.presentation.components.GlassButton
import com.vexono.app.presentation.components.GlassCard
import com.vexono.app.presentation.components.GlassSurface
import com.vexono.app.presentation.components.GlassTextField
import com.vexono.app.presentation.components.OccasionCategoryBadge
import com.vexono.app.presentation.components.PersianWheelYearPicker
import com.vexono.app.presentation.theme.LocalCustomColors
import com.vexono.app.presentation.viewmodel.OccasionsViewModel

@Composable
fun OccasionsScreen(
    viewModel: OccasionsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val customColors = LocalCustomColors.current
    var showYearWheelPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            GlassSurface(
                shape = RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp),
                backgroundColor = Color.White.copy(alpha = 0.05f),
                borderColor = Color.White.copy(alpha = 0.1f),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "مناسبت‌ها و تعطیلات رسمی",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Toggle Year Wheel Picker Button
                        GlassCard(
                            backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            onClick = { showYearWheelPicker = !showYearWheelPicker }
                        ) {
                            Text(
                                text = "سال ${JalaliCalendarEngine.toPersianDigits(uiState.selectedYear)} ▾",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Wheel Picker (1300 to 1500)
                    AnimatedVisibility(visible = showYearWheelPicker) {
                        Column(modifier = Modifier.padding(top = 10.dp).padding(horizontal = 16.dp)) {
                            Text(
                                text = "انتخاب سال شمسی (۱۳۰۰ تا ۱۵۰۰):",
                                style = MaterialTheme.typography.labelSmall,
                                color = customColors.textMuted
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            PersianWheelYearPicker(
                                selectedYear = uiState.selectedYear,
                                onYearSelected = {
                                    viewModel.setYear(it)
                                    showYearWheelPicker = false
                                },
                                startYear = 1300,
                                endYear = 1500
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Glassy Search Input
                    GlassTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = "جستجوی مناسبت (نوروز، فطر، یلدا، مبعث...)",
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
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Glassy Category Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            GlassCard(
                                backgroundColor = if (uiState.onlyHolidays) customColors.holidayColor.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.05f),
                                borderColor = if (uiState.onlyHolidays) customColors.holidayColor.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.1f),
                                onClick = { viewModel.toggleOnlyHolidays() }
                            ) {
                                Text(
                                    text = "فقط تعطیلات رسمی",
                                    fontSize = 11.sp,
                                    fontWeight = if (uiState.onlyHolidays) FontWeight.Bold else FontWeight.Normal,
                                    color = if (uiState.onlyHolidays) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        item {
                            val isAll = uiState.selectedCategory == null && !uiState.onlyHolidays
                            GlassCard(
                                backgroundColor = if (isAll) MaterialTheme.colorScheme.primary.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.05f),
                                borderColor = if (isAll) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.1f),
                                onClick = { viewModel.setCategory(null) }
                            ) {
                                Text(
                                    text = "همه دسته‌ها",
                                    fontSize = 11.sp,
                                    fontWeight = if (isAll) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isAll) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        listOf(
                            OccasionCategory.NATIONAL to "ملی و باستانی",
                            OccasionCategory.RELIGIOUS to "مذهبی و اعیاد",
                            OccasionCategory.OFFICIAL to "رسمی و تقویمی",
                            OccasionCategory.INTERNATIONAL to "بین‌المللی"
                        ).forEach { (cat, label) ->
                            val isSelected = uiState.selectedCategory == cat
                            item {
                                GlassCard(
                                    backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.05f),
                                    borderColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.1f),
                                    onClick = { viewModel.setCategory(if (isSelected) null else cat) }
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
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
        if (uiState.occasions.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.EventNote,
                title = "مناسبتی یافت نشد",
                description = "عبارت جستجو یا فیلتر دسته‌بندی را بررسی کنید.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            // Group occasions by month (1 to 12)
            val groupedByMonth = uiState.occasions.groupBy { it.month }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                groupedByMonth.forEach { (month, occasionsInMonth) ->
                    item {
                        val monthName = JalaliCalendarEngine.PERSIAN_MONTH_NAMES.getOrElse(month - 1) { "" }
                        Text(
                            text = "$monthName ${JalaliCalendarEngine.toPersianDigits(uiState.selectedYear)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                        )
                    }

                    items(occasionsInMonth, key = { it.id }) { occasion ->
                        GlassOccasionCard(occasion = occasion)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun GlassOccasionCard(
    occasion: Occasion
) {
    val customColors = LocalCustomColors.current

    GlassCard(
        shape = RoundedCornerShape(16.dp),
        backgroundColor = if (occasion.isHoliday) customColors.holidayColor.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.06f),
        borderColor = if (occasion.isHoliday) customColors.holidayColor.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.12f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Day Number Badge (Glassy)
            GlassSurface(
                shape = RoundedCornerShape(12.dp),
                backgroundColor = if (occasion.isHoliday) customColors.holidayColor.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.08f),
                borderColor = if (occasion.isHoliday) customColors.holidayColor.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.15f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = JalaliCalendarEngine.toPersianDigits(occasion.day),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (occasion.isHoliday) customColors.holidayColor else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = occasion.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (occasion.isHoliday) FontWeight.Bold else FontWeight.Medium,
                    color = if (occasion.isHoliday) customColors.holidayColor else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${JalaliCalendarEngine.toPersianDigits(occasion.day)} ${JalaliCalendarEngine.PERSIAN_MONTH_NAMES.getOrElse(occasion.month - 1) { "" }}",
                    fontSize = 12.sp,
                    color = customColors.textMuted
                )
            }

            OccasionCategoryBadge(
                category = occasion.category,
                isHoliday = occasion.isHoliday
            )
        }
    }
}
