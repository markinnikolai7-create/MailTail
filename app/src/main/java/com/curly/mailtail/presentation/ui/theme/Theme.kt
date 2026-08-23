package com.curly.mailtail.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
//    primary = PrimaryBlue,
//    secondary = PrimaryBlueVariant,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = AccentPink,
    onPrimary = Color.White,
    background = AppBackground,
    onBackground = TextPrimary,
    surface = PostSurface,
    onSurface = TextPrimary,
    surfaceVariant = BottomNavBackground,
    onSurfaceVariant = TextSecondary,
    outline = BorderPink
)

@Composable
fun MailTailTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Отключаем Dynamic Color по умолчанию, чтобы использовать свои цвета ТЗ
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}