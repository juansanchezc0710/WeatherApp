package com.example.weatherapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkBlueCenter = Color(0xFF1E3A5F)
val DarkBlueEdge = Color(0xFF0F1E2E)
val DarkBlueOuter = Color(0xFF050A0F)

val LightBlueCenter = Color(0xFF87CEEB)
val LightBlueEdge = Color(0xFF6BB6FF)
val LightBlueOuter = Color(0xFF4A9EFF)

val PrimaryBlue = Color(0xFF42A5F5)
val LightBlue = Color(0xFF64B5F6)

val GradientColorsLight = listOf(
    Color(0xFF3B82F6),
    Color(0xFF1E40AF)
)

val GradientColorsDark = listOf(
    Color(0xFF1E3A8A),
    Color(0xFF0F172A)
)

val IconOrange = Color(0xFFFF9800)
val IconOrangeLight = Color(0xFFFFB74D)
val IconOrangeDark = Color(0xFFFF5722)
val IconBlue = Color(0xFF2196F3)
val IconBlueLight = Color(0xFF64B5F6)

val ThermometerDark = Color(0xFFFF6B4A)
val ThermometerLight = Color(0xFFFF5722)

@Composable
fun getTextColor(): Color {
    return if (isSystemInDarkTheme()) {
        Color(0xFFE0E0E0)
    } else {
        Color.White
    }
}

@Composable
fun getCardColor(): Color {
    return if (isSystemInDarkTheme()) {
        Color(0xFF1E3A8A).copy(alpha = 0.6f)
    } else {
        Color.White.copy(alpha = 0.9f)
    }
}

@Composable
fun getCardTextColor(): Color {
    return if (isSystemInDarkTheme()) {
        Color(0xFFE0E0E0)
    } else {
        Color(0xFF212121)
    }
}

@Composable
fun getCardIconColor(): Color {
    return if (isSystemInDarkTheme()) {
        Color(0xFFE0E0E0).copy(alpha = 0.7f)
    } else {
        Color(0xFF757575)
    }
}

@Composable
fun getSearchFieldBackgroundColor(): Color {
    return if (isSystemInDarkTheme()) {
        Color(0xFF1E3A8A).copy(alpha = 0.8f)
    } else {
        Color.White
    }
}

@Composable
fun getSearchFieldTextColor(): Color {
    return if (isSystemInDarkTheme()) {
        Color(0xFFFFFFFF)
    } else {
        Color(0xFF212121)
    }
}

@Composable
fun getSearchFieldIconColor(): Color {
    return if (isSystemInDarkTheme()) {
        Color(0xFFFFFFFF).copy(alpha = 0.9f)
    } else {
        Color(0xFF757575)
    }
}

@Composable
fun getDialogColor(): Color {
    return if (isSystemInDarkTheme()) {
        Color(0xFF1E3A8A)
    } else {
        Color.White
    }
}