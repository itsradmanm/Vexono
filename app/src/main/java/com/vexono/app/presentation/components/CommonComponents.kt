package com.vexono.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vexono.app.data.calendar.JalaliCalendarEngine
import com.vexono.app.domain.model.CalendarDay
import com.vexono.app.domain.model.Occasion
import com.vexono.app.domain.model.OccasionCategory
import com.vexono.app.domain.model.Priority
import com.vexono.app.presentation.theme.LocalCustomColors

// ----------------------------------------------------
// 1. Glassmorphism Core Surfaces & Containers
// ----------------------------------------------------

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color? = null, // null = use theme-adaptive default
    borderColor: Color? = null,     // null = use theme-adaptive default
    borderWidth: Dp = 1.dp,
    shadowElevation: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    val customColors = LocalCustomColors.current
    val resolvedBackground = backgroundColor ?: MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
    val resolvedBorder = borderColor ?: customColors.glassSurfaceBorder

    Surface(
        shape = shape,
        color = resolvedBackground,
        border = BorderStroke(borderWidth, resolvedBorder),
        shadowElevation = shadowElevation,
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(18.dp),
    backgroundColor: Color? = null, // null = use theme-adaptive default
    borderColor: Color? = null,     // null = use theme-adaptive default
    borderWidth: Dp = 1.dp,
    shadowElevation: Dp = 6.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val customColors = LocalCustomColors.current
    val resolvedBackground = backgroundColor ?: customColors.glassCardBackground
    val resolvedBorder = borderColor ?: customColors.glassCardBorder

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    } else {
        Modifier
    }

    Surface(
        shape = shape,
        color = resolvedBackground,
        border = BorderStroke(borderWidth, resolvedBorder),
        shadowElevation = shadowElevation,
        modifier = modifier.then(clickModifier)
    ) {
        content()
    }
}

// ----------------------------------------------------
// 2. Glassmorphism Buttons (All app buttons are glassy)
// ----------------------------------------------------

@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(14.dp),
    containerColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    borderColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
    borderWidth: Dp = 1.dp,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
        ),
        border = BorderStroke(borderWidth, borderColor),
        contentPadding = contentPadding,
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun GlassOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(14.dp),
    containerColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
    borderWidth: Dp = 1.dp,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
        ),
        border = BorderStroke(borderWidth, borderColor),
        contentPadding = contentPadding,
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun GlassIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    containerColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
    borderWidth: Dp = 1.dp,
    size: Dp = 42.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(containerColor)
            .border(borderWidth, borderColor, shape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun GlassFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(18.dp),
    containerColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
    contentColor: Color = Color.White,
    borderColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
    size: Dp = 56.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(12.dp, shape, ambientColor = MaterialTheme.colorScheme.primary)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        containerColor,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                    )
                )
            )
            .border(1.dp, borderColor, shape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

// ----------------------------------------------------
// 3. Glassmorphism Text Field
// ----------------------------------------------------

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    shape: Shape = RoundedCornerShape(14.dp)
) {
    val customColors = LocalCustomColors.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = customColors.textMuted.copy(alpha = 0.7f),
                style = textStyle
            )
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        maxLines = maxLines,
        textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onBackground),
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    )
}

// ----------------------------------------------------
// 4. Calendar Day Cell (Glassy Theme - Adaptive)
// ----------------------------------------------------

@Composable
fun CalendarDayCell(
    day: CalendarDay,
    isSelected: Boolean,
    showGregorian: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f // heuristic for dark mode

    val textColor = when {
        !day.isCurrentMonth -> customColors.textMuted.copy(alpha = 0.3f)
        day.isHoliday -> customColors.holidayColor
        else -> MaterialTheme.colorScheme.onBackground
    }

    val gregorianTextColor = when {
        !day.isCurrentMonth -> customColors.textMuted.copy(alpha = 0.2f)
        day.isHoliday -> customColors.holidayColor.copy(alpha = 0.75f)
        else -> customColors.textMuted
    }

    val cellBackground = when {
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)
        day.isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        day.isCurrentMonth -> if (isDark) Color.White.copy(alpha = 0.04f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        else -> Color.Transparent
    }

    val borderModifier = when {
        isSelected -> Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
        day.isToday -> Modifier.border(1.2.dp, customColors.accentColor.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
        day.isCurrentMonth -> Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
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

            // Indicators row (Holidays, Occasions, Events, Tasks)
            if (day.isCurrentMonth && (day.eventCount > 0 || day.taskCount > 0 || day.occasions.isNotEmpty())) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    if (day.occasions.any { it.isHoliday }) {
                        Box(
                            modifier = Modifier
                                .size(4.5.dp)
                                .clip(CircleShape)
                                .background(customColors.holidayColor)
                        )
                    } else if (day.occasions.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(4.5.dp)
                                .clip(CircleShape)
                                .background(customColors.accentColor)
                        )
                    }

                    if (day.eventCount > 0) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Box(
                            modifier = Modifier
                                .size(4.5.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }

                    if (day.taskCount > 0) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Box(
                            modifier = Modifier
                                .size(4.5.dp)
                                .clip(CircleShape)
                                .background(if (day.completedTaskCount == day.taskCount) customColors.successColor else customColors.warningColor)
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 5. Glassy Occasion & Priority Badges
// ----------------------------------------------------

@Composable
fun OccasionCategoryBadge(
    category: OccasionCategory,
    isHoliday: Boolean,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    val (bgColor, textColor, borderColor, label) = when {
        isHoliday -> Tuple4(customColors.holidayColor.copy(alpha = 0.18f), customColors.holidayColor, customColors.holidayColor.copy(alpha = 0.35f), "تعطیل رسمی")
        category == OccasionCategory.NATIONAL -> Tuple4(customColors.accentColor.copy(alpha = 0.18f), customColors.accentColor, customColors.accentColor.copy(alpha = 0.35f), "ملی و باستانی")
        category == OccasionCategory.RELIGIOUS -> Tuple4(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f), MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), "مذهبی و اعیاد")
        category == OccasionCategory.OFFICIAL -> Tuple4(customColors.warningColor.copy(alpha = 0.18f), customColors.warningColor, customColors.warningColor.copy(alpha = 0.35f), "رسمی")
        else -> Tuple4(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), "بین‌المللی")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

@Composable
fun PriorityBadge(
    priority: Priority,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    val (bgColor, textColor, borderColor, label) = when (priority) {
        Priority.HIGH -> Tuple4(customColors.holidayColor.copy(alpha = 0.18f), customColors.holidayColor, customColors.holidayColor.copy(alpha = 0.35f), "بالا")
        Priority.MEDIUM -> Tuple4(customColors.warningColor.copy(alpha = 0.18f), customColors.warningColor, customColors.warningColor.copy(alpha = 0.35f), "متوسط")
        Priority.LOW -> Tuple4(customColors.accentColor.copy(alpha = 0.18f), customColors.accentColor, customColors.accentColor.copy(alpha = 0.35f), "پایین")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ----------------------------------------------------
// 6. Glassy Empty State View (Theme Adaptive)
// ----------------------------------------------------

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
        GlassSurface(
            shape = CircleShape,
            backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
            modifier = Modifier.size(76.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(38.dp)
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// ----------------------------------------------------
// 7. Glassy Occasion Bottom Sheet (Theme Adaptive)
// ----------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OccasionDetailBottomSheet(
    day: CalendarDay,
    onDismissRequest: () -> Unit,
    onViewDayDetail: () -> Unit,
    onAddEvent: () -> Unit,
    onAddTask: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val customColors = LocalCustomColors.current

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = customColors.dialogBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 4.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f))
            )
        },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .imePadding()
        ) {
            // Header: Date & Holiday Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = JalaliCalendarEngine.getFullPersianDateString(day.jalaliDate),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (day.isHoliday) customColors.holidayColor else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${JalaliCalendarEngine.getFullGregorianDateString(day.gregorianDate)}  •  ${JalaliCalendarEngine.getFullIslamicDateString(day.islamicDate)}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }

                if (day.isHoliday) {
                    OccasionCategoryBadge(
                        category = OccasionCategory.OFFICIAL,
                        isHoliday = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Occasions Section
            if (day.occasions.isNotEmpty()) {
                Text(
                    text = "مناسبت‌های امروز:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    day.occasions.forEach { occasion ->
                        GlassCard(
                            backgroundColor = if (occasion.isHoliday) customColors.holidayColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            borderColor = if (occasion.isHoliday) customColors.holidayColor.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = occasion.title,
                                    color = if (occasion.isHoliday) customColors.holidayColor else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = if (occasion.isHoliday) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OccasionCategoryBadge(
                                    category = occasion.category,
                                    isHoliday = occasion.isHoliday
                                )
                            }
                        }
                    }
                }
            } else {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "هیچ مناسبت رسمی برای این روز ثبت نشده است.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Events and Tasks count badge row
            if (day.eventCount > 0 || day.taskCount > 0) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (day.eventCount > 0) {
                        GlassCard(
                            backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = "📅 ${JalaliCalendarEngine.toPersianDigits(day.eventCount)} رویداد تنظیم‌شده",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                    if (day.taskCount > 0) {
                        GlassCard(
                            backgroundColor = customColors.accentColor.copy(alpha = 0.12f),
                            borderColor = customColors.accentColor.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = "✅ ${JalaliCalendarEngine.toPersianDigits(day.completedTaskCount)} از ${JalaliCalendarEngine.toPersianDigits(day.taskCount)} تسک انجام‌شده",
                                color = customColors.accentColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Glassy Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassButton(
                    onClick = {
                        onDismissRequest()
                        onAddEvent()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("افزودن رویداد", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                GlassOutlinedButton(
                    onClick = {
                        onDismissRequest()
                        onAddTask()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("افزودن تسک", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                GlassOutlinedButton(
                    onClick = {
                        onDismissRequest()
                        onViewDayDetail()
                    }
                ) {
                    Text("جزئیات", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
