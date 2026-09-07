package com.vexono.app.presentation.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vexono.app.presentation.components.GlassButton
import com.vexono.app.presentation.components.GlassCard
import com.vexono.app.presentation.components.GlassSurface
import com.vexono.app.presentation.theme.LocalCustomColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // 1. Logo Animation (Fade + Scale with natural CubicBezierEasing)
    val logoScale = remember { Animatable(0.7f) }
    val logoAlpha = remember { Animatable(0f) }

    // 2. Staggered Elements Animations (Slide-up + Fade-in with 100ms offset)
    val titleAlpha = remember { Animatable(0f) }
    val titleOffsetY = remember { Animatable(40f) }

    val subtitleAlpha = remember { Animatable(0f) }
    val subtitleOffsetY = remember { Animatable(40f) }

    val cardAlpha = remember { Animatable(0f) }
    val cardOffsetY = remember { Animatable(40f) }

    val buttonAlpha = remember { Animatable(0f) }
    val buttonOffsetY = remember { Animatable(40f) }

    val naturalEasing = remember { CubicBezierEasing(0.16f, 1.0f, 0.3f, 1.0f) }

    LaunchedEffect(Unit) {
        // Step 1: Logo Fade + Scale (600 - 800ms)
        launch {
            logoScale.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 750, easing = naturalEasing)
            )
        }
        launch {
            logoAlpha.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 650, easing = naturalEasing)
            )
        }

        delay(350)

        // Step 2: Staggered entrance for Title (0ms after delay)
        launch {
            titleAlpha.animateTo(1f, tween(500, easing = naturalEasing))
        }
        launch {
            titleOffsetY.animateTo(0f, tween(500, easing = naturalEasing))
        }

        delay(100)

        // Step 3: Staggered entrance for Subtitle (+100ms delay)
        launch {
            subtitleAlpha.animateTo(1f, tween(500, easing = naturalEasing))
        }
        launch {
            subtitleOffsetY.animateTo(0f, tween(500, easing = naturalEasing))
        }

        delay(100)

        // Step 4: Staggered entrance for Glass Feature Card (+100ms delay)
        launch {
            cardAlpha.animateTo(1f, tween(500, easing = naturalEasing))
        }
        launch {
            cardOffsetY.animateTo(0f, tween(500, easing = naturalEasing))
        }

        delay(100)

        // Step 5: Staggered entrance for Button (+100ms delay)
        launch {
            buttonAlpha.animateTo(1f, tween(500, easing = naturalEasing))
        }
        launch {
            buttonOffsetY.animateTo(0f, tween(500, easing = naturalEasing))
        }

        // Auto transition after smooth presentation
        delay(1400)
        onSplashFinished()
    }

    val customColors = LocalCustomColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0D14),
                        Color(0xFF141420),
                        Color(0xFF0F0F16)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Glowing background ambient circles
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(y = (-80).dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 80.dp, y = 140.dp)
                .clip(CircleShape)
                .background(customColors.accentColor.copy(alpha = 0.08f))
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
        ) {
            // 1. Logo (Fade + Scale)
            GlassSurface(
                shape = RoundedCornerShape(32.dp),
                backgroundColor = Color.White.copy(alpha = 0.09f),
                borderColor = Color.White.copy(alpha = 0.22f),
                borderWidth = 1.2.dp,
                shadowElevation = 18.dp,
                modifier = Modifier
                    .size(108.dp)
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                    customColors.accentColor.copy(alpha = 0.65f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Vexono Logo",
                        tint = Color.White,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // 2. Title (Staggered Slide-up + Fade-in)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(titleAlpha.value)
                    .offset { IntOffset(0, titleOffsetY.value.toInt()) }
            ) {
                Text(
                    text = "Vexono",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = 3.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Subtitle (Staggered Slide-up + Fade-in)
            Text(
                text = "تقویم هوشمند شمسی، مناسبت‌ها و مدیریت وظایف",
                style = MaterialTheme.typography.bodyMedium,
                color = customColors.textMuted,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(subtitleAlpha.value)
                    .offset { IntOffset(0, subtitleOffsetY.value.toInt()) }
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 4. Feature Card (Staggered Glassy Preview)
            GlassCard(
                backgroundColor = Color.White.copy(alpha = 0.06f),
                borderColor = Color.White.copy(alpha = 0.12f),
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(cardAlpha.value)
                    .offset { IntOffset(0, cardOffsetY.value.toInt()) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SplashFeatureItem(
                        icon = Icons.Default.CalendarMonth,
                        title = "۱۳۰۰ تا ۱۵۰۰",
                        subtitle = "پوشش ۲۰۰ ساله"
                    )
                    SplashFeatureItem(
                        icon = Icons.Default.NotificationsActive,
                        title = "مناسبت‌ها",
                        subtitle = "رسمی و ملی"
                    )
                    SplashFeatureItem(
                        icon = Icons.Default.CheckCircle,
                        title = "تسک‌ها",
                        subtitle = "برنامه‌ریزی دقیق"
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 5. Entry Glass Button
            GlassButton(
                onClick = onSplashFinished,
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .alpha(buttonAlpha.value)
                    .offset { IntOffset(0, buttonOffsetY.value.toInt()) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ورود به تقویم",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowBack, // RTL back is forward
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SplashFeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    val customColors = LocalCustomColors.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = customColors.accentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitle,
            fontSize = 10.sp,
            color = customColors.textMuted
        )
    }
}
