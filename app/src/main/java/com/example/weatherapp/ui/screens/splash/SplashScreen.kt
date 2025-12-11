package com.example.weatherapp.ui.screens.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.GradientColorsDark
import com.example.weatherapp.ui.theme.GradientColorsLight
import com.example.weatherapp.ui.theme.getTextColor
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.delay

private enum class SplashWeatherIconType(val iconResId: Int) {
    CLEAR_DAY(R.drawable.ic_sun_clear_day),
    PARTLY_CLOUDY(R.drawable.ic_partly_cloudy),
    THUNDERSTORM(R.drawable.ic_thunderstorm)
}

private const val ICON_CHANGE_INTERVAL_MS = 1200L
private const val NUMBER_OF_ICONS = 3
private const val SPLASH_DURATION_MS = ICON_CHANGE_INTERVAL_MS * NUMBER_OF_ICONS - 200L
private const val FADE_TRANSITION_MS = 200
private const val ICON_CHANGE_DELAY_MS = 200L
private const val SUN_ROTATION_DURATION_MS = 8000

@Composable
private fun getGradientColors(): List<Color> {
    return if (isSystemInDarkTheme()) {
        GradientColorsDark
    } else {
        GradientColorsLight
    }
}

@Composable
fun SplashScreen(
    onNavigateToSearch: () -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "splashAlpha"
    )

    LaunchedEffect(key1 = true) {
        delay(SPLASH_DURATION_MS)
        isVisible = false
        delay(1000)
        onNavigateToSearch()
    }

    if (alphaAnim.value > 0f) {
        Splash(alpha = alphaAnim.value)
    }
}

@Composable
private fun Splash(alpha: Float) {
    var currentIconIndex by remember { mutableIntStateOf(0) }
    var isVisible by remember { mutableStateOf(true) }
    val weatherIcons = SplashWeatherIconType.entries
    val gradientColors = getGradientColors()
    val iconColor = getTextColor()
    val textColor = getTextColor()

    val sunRotation by rememberInfiniteTransition(label = "sunRotation").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(SUN_ROTATION_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sunRotation"
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(ICON_CHANGE_INTERVAL_MS)
            isVisible = false
            delay(ICON_CHANGE_DELAY_MS)
            currentIconIndex = (currentIconIndex + 1) % weatherIcons.size
            isVisible = true
        }
    }

    val iconAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = FADE_TRANSITION_MS),
        label = "iconAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = gradientColors))
            .alpha(alpha = alpha),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            WeatherIcon(
                icon = weatherIcons[currentIconIndex],
                modifier = Modifier.size(120.dp),
                alpha = iconAlpha,
                rotation = if (weatherIcons[currentIconIndex] == SplashWeatherIconType.CLEAR_DAY) sunRotation else 0f,
                iconColor = iconColor
            )

            Text(
                text = "WeatherApp",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 32.dp)
            )

            Text(
                text = "Tu pronóstico del clima",
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = textColor.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun WeatherIcon(
    icon: SplashWeatherIconType,
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    rotation: Float = 0f,
    iconColor: Color = Color.White
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = icon.iconResId),
            contentDescription = "Weather Icon",
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha)
                .rotate(rotation),
            tint = iconColor
        )
    }
}

@Preview(
    name = "Splash Screen Light",
    showBackground = true,
    backgroundColor = 0xFF3B82F6,
    showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun SplashScreenLightPreview() {
    WeatherAppTheme(darkTheme = false) {
        Splash(alpha = 1f)
    }
}

@Preview(
    name = "Splash Screen Dark",
    showBackground = true,
    backgroundColor = 0xFF0F172A,
    showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun SplashScreenDarkPreview() {
    WeatherAppTheme(darkTheme = true) {
        Splash(alpha = 1f)
    }
}
