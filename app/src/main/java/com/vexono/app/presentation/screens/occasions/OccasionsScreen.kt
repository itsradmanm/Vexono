package com.vexono.app.presentation.screens.occasions

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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.vexono.app.presentation.components.OccasionCategoryBadge
import com.vexono.app.presentation.theme.LocalCustomColors
import com.vexono.app.presentation.viewmodel.OccasionsViewModel

@Composable
fun OccasionsScreen(
    viewModel: OccasionsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val customColors = LocalCustomColors.current

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
                        text = "مناسبت‌ها و تعطیلات ۱۰ ساله",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 10-Year Switcher Row
                    Text(
                        text = "انتخاب سال شمسی:",
                        style = MaterialTheme.typography.labelSmall,
                        color = customColors.textMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uiState.availableYears) { year ->
                            val isSelected = year == uiState.selectedYear
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { viewModel.setYear(year) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = JalaliCalendarEngine.toPersianDigits(year),
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search input
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("جستجوی مناسبت (نوروز، فطر، یلدا...)") },
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = uiState.onlyHolidays,
                                onClick = { viewModel.toggleOnlyHolidays() },
                                label = { Text("فقط تعطیلات رسمی", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = customColors.holidayColor,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }

                        item {
                            FilterChip(
                                selected = uiState.selectedCategory == null && !uiState.onlyHolidays,
                                onClick = { viewModel.setCategory(null) },
                                label = { Text("همه دسته‌ها", fontSize = 11.sp) }
                            )
                        }

                        listOf(
                            OccasionCategory.NATIONAL to "ملی و باستانی",
                            OccasionCategory.RELIGIOUS to "مذهبی و اعیاد",
                            OccasionCategory.OFFICIAL to "رسمی و دولتی",
                            OccasionCategory.INTERNATIONAL to "بین‌المللی"
                        ).forEach { (cat, label) ->
                            item {
                                FilterChip(
                                    selected = uiState.selectedCategory == cat,
                                    onClick = { viewModel.setCategory(if (uiState.selectedCategory == cat) null else cat) },
                                    label = { Text(label, fontSize = 11.sp) }
                                )
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                groupedByMonth.forEach { (month, occasionsInMonth) ->
                    item {
                        val monthName = JalaliCalendarEngine.PERSIAN_MONTH_NAMES.getOrElse(month - 1) { "" }
                        Text(
                            text = "$monthName ${JalaliCalendarEngine.toPersianDigits(uiState.selectedYear)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                        )
                    }

                    items(occasionsInMonth, key = { it.id }) { occasion ->
                        OccasionListItemCard(occasion = occasion)
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
private fun OccasionListItemCard(
    occasion: Occasion
) {
    val customColors = LocalCustomColors.current

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
            // Day Number Badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (occasion.isHoliday) customColors.holidayColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(width = 46.dp, height = 46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = JalaliCalendarEngine.toPersianDigits(occasion.day),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
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
                    color = if (occasion.isHoliday) customColors.holidayColor else MaterialTheme.colorScheme.onSurface
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
