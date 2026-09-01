package com.vexono.app.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat
import com.vexono.app.domain.model.ThemeMode

data class CustomColorsPalette(
    val holidayColor: Color = VexonoHolidayRed,
    val accentColor: Color = VexonoAccentCyan,
    val warningColor: Color = VexonoWarningAmber,
    val successColor: Color = VexonoSuccessGreen,
    val surfaceElevated: Color = VexonoDarkSurfaceElevated,
    val textMuted: Color = VexonoTextMuted
)

val LocalCustomColors = staticCompositionLocalOf { CustomColorsPalette() }

@Composable
fun VexonoTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    primaryColorHex: String = "#7C4DFF",
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val brandColor = runCatching {
        Color(android.graphics.Color.parseColor(primaryColorHex))
    }.getOrDefault(VexonoPrimaryViolet)

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = brandColor,
            onPrimary = Color.White,
            secondary = VexonoAccentCyan,
            onSecondary = Color.Black,
            tertiary = VexonoWarningAmber,
            background = VexonoDarkBackground,
            onBackground = VexonoTextPrimary,
            surface = VexonoDarkSurface,
            onSurface = VexonoTextPrimary,
            surfaceVariant = VexonoDarkSurfaceVariant,
            onSurfaceVariant = VexonoTextSecondary,
            outline = VexonoDarkBorder,
            error = VexonoHolidayRed,
            onError = Color.White
        )
    } else {
        lightColorScheme(
            primary = brandColor,
            onPrimary = Color.White,
            secondary = Color(0xFF00BFA5),
            onSecondary = Color.White,
            tertiary = Color(0xFFE65100),
            background = VexonoLightBackground,
            onBackground = VexonoLightTextPrimary,
            surface = VexonoLightSurface,
            onSurface = VexonoLightTextPrimary,
            surfaceVariant = VexonoLightSurfaceVariant,
            onSurfaceVariant = VexonoLightTextSecondary,
            outline = VexonoLightBorder,
            error = VexonoHolidayRed,
            onError = Color.White
        )
    }

    val customColors = if (isDark) {
        CustomColorsPalette(
            holidayColor = VexonoHolidayRed,
            accentColor = VexonoAccentCyan,
            warningColor = VexonoWarningAmber,
            successColor = VexonoSuccessGreen,
            surfaceElevated = VexonoDarkSurfaceElevated,
            textMuted = VexonoTextMuted
        )
    } else {
        CustomColorsPalette(
            holidayColor = VexonoHolidayRed,
            accentColor = Color(0xFF00BFA5),
            warningColor = Color(0xFFE65100),
            successColor = Color(0xFF059669),
            surfaceElevated = VexonoLightSurfaceElevated,
            textMuted = VexonoLightTextMuted
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !isDark
                isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl,
        LocalCustomColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = VexonoTypography,
            content = content
        )
    }
}
