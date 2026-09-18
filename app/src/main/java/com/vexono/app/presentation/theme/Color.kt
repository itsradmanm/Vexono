package com.vexono.app.presentation.theme

import androidx.compose.ui.graphics.Color

// Default Dark Theme Colors (Vexono Signature Dark)
val VexonoDarkBackground = Color(0xFF0C0C12)
val VexonoDarkSurface = Color(0xFF1B1B24)
val VexonoDarkSurfaceElevated = Color(0xFF242430)
val VexonoDarkSurfaceVariant = Color(0xFF2A2A38)
val VexonoDarkBorder = Color(0xFF333345)

// Bottom bar / dialog backgrounds for dark mode
val VexonoDarkBottomBar = Color(0xFF13131E)

val VexonoPrimaryViolet = Color(0xFF7C4DFF)
val VexonoAccentCyan = Color(0xFF00E5C7)
val VexonoHolidayRed = Color(0xFFFF5C7A)
val VexonoWarningAmber = Color(0xFFFFB300)
val VexonoSuccessGreen = Color(0xFF10B981)

val VexonoTextPrimary = Color(0xFFF5F5F7)
val VexonoTextSecondary = Color(0xFF9E9EA8)
val VexonoTextMuted = Color(0xFF6E6E7A)

// Light Mode (Frosted Glass Elite)
val VexonoLightBackground = Color(0xFFF4F6FB)
val VexonoLightSurface = Color(0xFFFFFFFF)
val VexonoLightSurfaceElevated = Color(0xFFF0F1F7)
val VexonoLightSurfaceVariant = Color(0xFFE8EAF4)
val VexonoLightBorder = Color(0xFFE2E8F0)
val VexonoLightTextPrimary = Color(0xFF1A1A2E)
val VexonoLightTextSecondary = Color(0xFF4A4A6A)
val VexonoLightTextMuted = Color(0xFF7A7A9A)

// Light mode bottom bar / dialog backgrounds - crisp white frosted glass
val VexonoLightBottomBar = Color(0xFFF8F9FE)
val VexonoLightDialogBackground = Color(0xFFFCFDFF)

// Brand Color Presets
enum class BrandColorPreset(val title: String, val hex: String, val color: Color) {
    VIOLET("بنفش نئونی (پیش‌فرض)", "#7C4DFF", Color(0xFF7C4DFF)),
    CYAN("فیروزه‌ای الکتریک", "#00E5C7", Color(0xFF00E5C7)),
    EMERALD("سبز زمردی", "#10B981", Color(0xFF10B981)),
    AMBER("طلایی گرم", "#FFB300", Color(0xFFFFB300)),
    ROSE("رز آتشین", "#F43F5E", Color(0xFFF43F5E)),
    INDIGO("نیلی سلطنتی", "#4F46E5", Color(0xFF4F46E5));

    companion object {
        fun fromHex(hex: String): BrandColorPreset {
            return entries.find { it.hex.equals(hex, ignoreCase = true) } ?: VIOLET
        }
    }
}
