package com.vexono.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vexono.app.data.calendar.JalaliCalendarEngine
import com.vexono.app.domain.model.CalendarDay
import com.vexono.app.domain.model.OccasionCategory
import com.vexono.app.domain.model.Priority
import com.vexono.app.presentation.theme.LocalCustomColors

@Composable
fun CalendarDayCell(
    day: CalendarDay,
    isSelected: Boolean,
    showGregorian: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current

    val textColor = when {
        !day.isCurrentMonth -> customColors.textMuted.copy(alpha = 0.35f)
        day.isHoliday -> customColors.holidayColor
        else -> MaterialTheme.colorScheme.onBackground
    }

    val gregorianTextColor = when {
        !day.isCurrentMonth -> customColors.textMuted.copy(alpha = 0.25f)
        day.isHoliday -> customColors.holidayColor.copy(alpha = 0.7f)
        else -> customColors.textMuted
    }

    val cellBackground = when {
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
        day.isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        else -> Color.Transparent
    }

    val borderModifier = when {
        isSelected -> Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(14.dp))
        day.isToday -> Modifier.border(1.2.dp, customColors.accentColor, RoundedCornerShape(14.dp))
        else -> Modifier
    }

    Box(
        modifier = modifier
            .padding(2.dp)
            .height(58.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(cellBackground)
            .then(borderModifier)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(2.dp)
        ) {
            // Main Jalali Day Number
            Text(
                text = JalaliCalendarEngine.toPersianDigits(day.jalaliDate.day),
                color = textColor,
                fontSize = 17.sp,
                fontWeight = if (day.isToday || isSelected) FontWeight.Bold else FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            // Companion Gregorian Day Number
            if (showGregorian) {
                Text(
                    text = day.gregorianDate.day.toString(),
                    color = gregorianTextColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }

            // Indicators row (Events & Tasks dots)
            if (day.isCurrentMonth && (day.eventCount > 0 || day.taskCount > 0 || day.occasions.isNotEmpty())) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    if (day.occasions.any { it.isHoliday }) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(customColors.holidayColor)
                        )
                    } else if (day.occasions.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(customColors.accentColor)
                        )
                    }

                    if (day.eventCount > 0) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }

                    if (day.taskCount > 0) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(if (day.completedTaskCount == day.taskCount) customColors.successColor else customColors.warningColor)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OccasionCategoryBadge(
    category: OccasionCategory,
    isHoliday: Boolean,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    val (bgColor, textColor, label) = when {
        isHoliday -> Triple(customColors.holidayColor.copy(alpha = 0.15f), customColors.holidayColor, "تعطیل رسمی")
        category == OccasionCategory.NATIONAL -> Triple(customColors.accentColor.copy(alpha = 0.15f), customColors.accentColor, "ملی")
        category == OccasionCategory.RELIGIOUS -> Triple(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), MaterialTheme.colorScheme.primary, "مذهبی")
        category == OccasionCategory.OFFICIAL -> Triple(customColors.warningColor.copy(alpha = 0.15f), customColors.warningColor, "رسمی")
        else -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, "بین‌المللی")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun PriorityBadge(
    priority: Priority,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    val (bgColor, textColor, label) = when (priority) {
        Priority.HIGH -> Triple(customColors.holidayColor.copy(alpha = 0.15f), customColors.holidayColor, "بالا")
        Priority.MEDIUM -> Triple(customColors.warningColor.copy(alpha = 0.15f), customColors.warningColor, "متوسط")
        Priority.LOW -> Triple(customColors.accentColor.copy(alpha = 0.15f), customColors.accentColor, "پایین")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = LocalCustomColors.current.textMuted,
            textAlign = TextAlign.Center
        )
    }
}
