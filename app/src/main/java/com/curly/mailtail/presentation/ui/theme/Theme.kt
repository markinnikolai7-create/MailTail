package com.curly.mailtail.presentation.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AccentPink,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkBottomNav,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkIconUnselected
)

private val LightColorScheme = lightColorScheme(
    primary = AccentPink,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightBottomNav,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    outlineVariant = LightIconUnselected
)

@Composable
fun MailTailTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Верхняя статус-бар панель (где часы и батарея)
            window.statusBarColor = colorScheme.background.toArgb()
            // Нижняя панель навигации (где кнопки телефона)
            window.navigationBarColor = colorScheme.surfaceVariant.toArgb()
            // Цвет иконок статуса (черные на светлом, белые на темном)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}