package com.vexono.app.presentation.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vexono.app.presentation.theme.LocalCustomColors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 800)
        )
        delay(1200)
        onSplashFinished()
    }

    val customColors = LocalCustomColors.current
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            customColors.accentColor
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            // Glowing Icon Frame
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 12.dp,
                modifier = Modifier.size(100.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradientBrush),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Vexono Logo",
                        tint = Color.White,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Name
            Text(
                text = "Vexono",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "تقویم هوشمند شمسی و میلادی",
                style = MaterialTheme.typography.bodyMedium,
                color = customColors.textMuted,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
