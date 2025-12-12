package com.example.weatherapp.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import coil.compose.AsyncImage
import com.example.weatherapp.R
import com.example.weatherapp.domain.model.CurrentWeather
import com.example.weatherapp.domain.model.ForecastDay
import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.ui.theme.GradientColorsDark
import com.example.weatherapp.ui.theme.GradientColorsLight
import com.example.weatherapp.ui.theme.IconBlue
import com.example.weatherapp.ui.theme.IconBlueLight
import com.example.weatherapp.ui.theme.IconOrange
import com.example.weatherapp.ui.theme.IconOrangeDark
import com.example.weatherapp.ui.theme.IconOrangeLight
import com.example.weatherapp.ui.theme.ThermometerDark
import com.example.weatherapp.ui.theme.ThermometerLight
import com.example.weatherapp.ui.theme.getCardColor
import com.example.weatherapp.ui.theme.getCardTextColor
import com.example.weatherapp.ui.theme.getTextColor
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.ui.viewmodel.WeatherDetailsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
private fun getGradientColors(): List<Color> {
    return if (isSystemInDarkTheme()) {
        GradientColorsDark
    } else {
        GradientColorsLight
    }
}

/**
 * Weather detail screen showing current weather and forecast.
 *
 * @param locationName Name of the location to display weather for
 * @param onBackClick Callback when back button is clicked
 */
@Composable
fun WeatherDetailScreen(
    locationName: String,
    onBackClick: () -> Unit,
    viewModel: WeatherDetailsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var pullOffset by remember { mutableFloatStateOf(0f) }
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(locationName) {
        if (locationName.isNotBlank()) {
            viewModel.loadWeatherForecastData(locationName)
        }
    }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            isRefreshing = false
        }
    }

    val gradientColors = getGradientColors()
    val textColor = getTextColor()
    val cardColor = getCardColor()
    val cardTextColor = getCardTextColor()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = gradientColors
                )
            )
    ) {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = textColor
                        )
                    }
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragEnd = {
                                    if (pullOffset > 100f && !isRefreshing) {
                                        isRefreshing = true
                                        viewModel.refreshWeatherForecastData()
                                    }
                                    pullOffset = 0f
                                }
                            ) { change, dragAmount ->
                                if (change.position.y > 0 && dragAmount.y > 0) {
                                    pullOffset = (pullOffset + dragAmount.y).coerceAtMost(150f)
                                } else if (pullOffset > 0) {
                                    pullOffset = (pullOffset + dragAmount.y).coerceAtLeast(0f)
                                }
                            }
                        }
                ) {
                    when {
                        uiState.isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = textColor)
                            }
                        }

                        uiState.error != null -> {
                            val scrollState = rememberScrollState()
                            
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = textColor.copy(alpha = 0.7f)
                                        )
                                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                        Text(
                                            text = "Desliza para recargar",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = textColor.copy(alpha = 0.7f),
                                            textAlign = TextAlign.Center,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(1f)
                                        .verticalScroll(scrollState)
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp, vertical = 16.dp),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = cardColor
                                        )
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(32.dp),
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(80.dp)
                                                    .background(
                                                        brush = Brush.radialGradient(
                                                            colors = listOf(
                                                                IconOrange.copy(alpha = 0.2f),
                                                                IconOrangeDark.copy(alpha = 0.1f)
                                                            )
                                                        ),
                                                        shape = RoundedCornerShape(40.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (uiState.error == "Sin conexión a internet") {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.ic_no_internet_connection),
                                                        contentDescription = "Sin conexión",
                                                        tint = if (isSystemInDarkTheme()) {
                                                            IconOrangeLight
                                                        } else {
                                                            IconOrange
                                                        },
                                                        modifier = Modifier.size(48.dp)
                                                    )
                                                } else {
                                                    Icon(
                                                        imageVector = Icons.Default.Warning,
                                                        contentDescription = "Error",
                                                        tint = if (isSystemInDarkTheme()) {
                                                            IconOrangeLight
                                                        } else {
                                                            IconOrange
                                                        },
                                                        modifier = Modifier.size(48.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = if (uiState.error == "Sin conexión a internet") {
                                                    "Sin conexión a internet"
                                                } else {
                                                    "Error"
                                                },
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = cardTextColor,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = if (uiState.error == "Sin conexión a internet") {
                                                    "No se pudo establecer conexión con el servidor"
                                                } else {
                                                    uiState.error ?: "Ocurrió un error. Por favor intenta nuevamente"
                                                },
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = cardTextColor.copy(alpha = 0.7f),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.padding(bottom = 32.dp))
                                }
                            }
                        }

                        uiState.weatherData != null -> {
                            uiState.weatherData?.let { weatherData ->
                                val configuration = LocalConfiguration.current
                                val isLandscape =
                                    configuration.screenWidthDp > configuration.screenHeightDp
                                val forecastDays = weatherData.forecast.take(3)

                                if (isLandscape) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .verticalScroll(rememberScrollState()),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            WeatherLocationHeader(
                                                weatherData.locationName,
                                                textColor
                                            )
                                            CurrentWeatherInfoCard(
                                                weatherData.current,
                                                textColor,
                                                cardColor,
                                                cardTextColor
                                            )
                                        }
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .verticalScroll(rememberScrollState()),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = "Pronóstico (3 días)",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = textColor,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                            forecastDays.forEach { forecastDay ->
                                                WeatherForecastDayCard(
                                                    forecastDay,
                                                    cardColor,
                                                    cardTextColor
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        contentPadding = PaddingValues(vertical = 4.dp)
                                    ) {
                                        item {
                                            WeatherLocationHeader(
                                                weatherData.locationName,
                                                textColor
                                            )
                                        }

                                        item {
                                            CurrentWeatherInfoCard(
                                                weatherData.current,
                                                textColor,
                                                cardColor,
                                                cardTextColor
                                            )
                                        }

                                        item {
                                            Text(
                                                text = "Pronóstico (3 días)",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = textColor,
                                                modifier = Modifier.padding(
                                                    horizontal = 24.dp,
                                                    vertical = 8.dp
                                                )
                                            )
                                        }

                                        items(forecastDays) { forecastDay ->
                                            WeatherForecastDayCard(
                                                forecastDay,
                                                cardColor,
                                                cardTextColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        else -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = textColor)
                            }
                        }
                    }
                }

                if (uiState.error != null && (pullOffset > 0 || uiState.isLoading)) {
                    val rotation by animateFloatAsState(
                        targetValue = if (pullOffset > 60f) 180f else 0f,
                        animationSpec = tween(durationMillis = 200),
                        label = "rotation"
                    )
                    val alpha by animateFloatAsState(
                        targetValue = if (pullOffset > 0 || uiState.isLoading) 1f else 0f,
                        animationSpec = tween(durationMillis = 200),
                        label = "alpha"
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp)
                            .alpha(alpha)
                    ) {
                        if (uiState.isLoading && pullOffset == 0f) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = textColor
                            )
                        } else if (pullOffset > 0) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Deslizar para recargar",
                                    modifier = Modifier
                                        .size(20.dp)
                                        .rotate(rotation),
                                    tint = textColor.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = if (pullOffset > 60f) "Suelta" else "Desliza",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textColor.copy(alpha = 0.6f),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Header component displaying the location name.
 *
 * @param locationName Name of the location
 * @param textColor Color for the text
 */
@Composable
private fun WeatherLocationHeader(
    locationName: String,
    textColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = locationName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

/**
 * Card displaying current weather information.
 *
 * @param current Current weather data
 * @param textColor Color for text elements
 * @param cardColor Background color for the card
 * @param cardTextColor Text color for card content
 */
@Composable
private fun CurrentWeatherInfoCard(
    current: CurrentWeather,
    textColor: Color,
        cardColor: Color = getCardColor(),
        cardTextColor: Color = getCardTextColor()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardColor
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hoy",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = cardTextColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            AsyncImage(
                model = "https:${current.conditionIcon}",
                contentDescription = current.condition,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${current.temperature.toInt()}°C",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = cardTextColor
            )

            Text(
                text = current.condition,
                style = MaterialTheme.typography.titleMedium,
                color = cardTextColor.copy(alpha = 0.8f),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailInfoCard(
                    iconType = WeatherDetailIconType.THERMOMETER,
                    label = "Sensación",
                    value = "${current.feelsLike.toInt()}°C",
                    iconColor = getWeatherDetailIconColor(WeatherDetailIconType.THERMOMETER),
                    cardColor = cardColor,
                    textColor = cardTextColor
                )
                WeatherDetailInfoCard(
                    iconType = WeatherDetailIconType.WATER_DROP,
                    label = "Humedad",
                    value = "${current.humidity}%",
                    iconColor = getWeatherDetailIconColor(WeatherDetailIconType.WATER_DROP),
                    cardColor = cardColor,
                    textColor = cardTextColor
                )
                WeatherDetailInfoCard(
                    iconType = WeatherDetailIconType.WIND,
                    label = "Viento",
                    value = "${current.windSpeed.toInt()} km/h",
                    iconColor = getWeatherDetailIconColor(WeatherDetailIconType.WIND),
                    cardColor = cardColor,
                    textColor = cardTextColor
                )
            }
        }
    }
}

/**
 * Card displaying a weather detail (temperature, humidity, wind).
 *
 * @param iconType Type of icon to display
 * @param label Label text
 * @param value Value text
 * @param iconColor Color for the icon
 * @param cardColor Background color
 * @param textColor Text color
 */
@Composable
private fun WeatherDetailInfoCard(
    iconType: WeatherDetailIconType,
    label: String,
    value: String,
    iconColor: Color,
    cardColor: Color,
    textColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        WeatherDetailIcon(
            iconType = iconType,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = textColor.copy(alpha = 0.7f)
        )
    }
}

private enum class WeatherDetailIconType(val iconResId: Int) {
    THERMOMETER(R.drawable.ic_thermometer),
    WATER_DROP(R.drawable.ic_water_drop),
    WIND(R.drawable.ic_wind)
}

@Composable
private fun getWeatherDetailIconColor(iconType: WeatherDetailIconType): Color {
    return when (iconType) {
        WeatherDetailIconType.THERMOMETER -> {
            if (isSystemInDarkTheme()) {
                ThermometerDark
            } else {
                ThermometerLight
            }
        }

        WeatherDetailIconType.WATER_DROP -> {
            if (isSystemInDarkTheme()) {
                IconBlueLight
            } else {
                IconBlue
            }
        }

        WeatherDetailIconType.WIND -> {
            if (isSystemInDarkTheme()) {
                Color(0xFFB0BEC5)
            } else {
                Color(0xFF757575)
            }
        }
    }
}

/**
 * Icon component for weather details.
 *
 * @param iconType Type of icon to display
 * @param modifier Modifier for the icon
 * @param tint Color tint for the icon
 */
@Composable
private fun WeatherDetailIcon(
    iconType: WeatherDetailIconType,
    modifier: Modifier = Modifier,
    tint: Color = getWeatherDetailIconColor(iconType)
) {
    Icon(
        painter = painterResource(id = iconType.iconResId),
        contentDescription = when (iconType) {
            WeatherDetailIconType.THERMOMETER -> "Termómetro"
            WeatherDetailIconType.WATER_DROP -> "Gota de agua"
            WeatherDetailIconType.WIND -> "Viento"
        },
        modifier = modifier,
        tint = tint
    )
}

/**
 * Card displaying forecast information for a single day.
 *
 * @param forecastDay Forecast data for the day
 * @param cardColor Background color for the card
 * @param textColor Text color
 */
@Composable
private fun WeatherForecastDayCard(
    forecastDay: ForecastDay,
    cardColor: Color,
    textColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatForecastDate(forecastDay.date),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = forecastDay.condition,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WeatherDetailIcon(
                        iconType = WeatherDetailIconType.WATER_DROP,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Humedad: ${forecastDay.avgHumidity}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
            }

            AsyncImage(
                model = "https:${forecastDay.conditionIcon}",
                contentDescription = forecastDay.condition,
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${forecastDay.maxTemp.toInt()}°",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        text = "/",
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "${forecastDay.minTemp.toInt()}°",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = "Prom: ${forecastDay.avgTemp.toInt()}°C",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

/**
 * Formats a date string from "YYYY-MM-DD" to "DD Mon" format.
 *
 * @param dateString Date string in "YYYY-MM-DD" format
 * @return Formatted date string
 */
private fun formatForecastDate(dateString: String): String {
    return try {
        val parts = dateString.split("-")
        if (parts.size == 3) {
            val day = parts[2].toInt()
            val month = parts[1].toInt()
            val monthNames = listOf(
                "Ene", "Feb", "Mar", "Abr", "May", "Jun",
                "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
            )
            "$day ${monthNames.getOrNull(month - 1) ?: month}"
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}

@Preview(
    name = "Weather Details - Light Mode",
    showBackground = true,
    backgroundColor = 0xFF3B82F6,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun WeatherDetailsLightPreview() {
    WeatherAppTheme(darkTheme = false) {
        WeatherDetailsWithDataPreview()
    }
}

@Preview(
    name = "Weather Details - Dark Mode",
    showBackground = true,
    backgroundColor = 0xFF0F172A,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun WeatherDetailsDarkPreview() {
    WeatherAppTheme(darkTheme = true) {
        WeatherDetailsWithDataPreview()
    }
}

@Composable
private fun WeatherDetailsWithDataPreview() {
    val mockWeatherData = Weather(
        locationName = "Bogotá",
        current = CurrentWeather(
            temperature = 18.0,
            condition = "Parcialmente nublado",
            conditionIcon = "//cdn.weatherapi.com/weather/64x64/day/116.png",
            feelsLike = 17.0,
            humidity = 65,
            windSpeed = 12.5,
            pressure = 1013.0
        ),
        forecast = listOf(
            ForecastDay(
                date = "2024-01-15",
                maxTemp = 20.0,
                minTemp = 12.0,
                avgTemp = 16.0,
                condition = "Parcialmente nublado",
                conditionIcon = "//cdn.weatherapi.com/weather/64x64/day/116.png",
                avgHumidity = 65
            )
        )
    )

    val gradientColors = getGradientColors()
    val textColor = getTextColor()
    val cardColor = getCardColor()
    val cardTextColor = getCardTextColor()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp
    val forecastDays = mockWeatherData.forecast.take(3)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = gradientColors
                )
            )
    ) {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = textColor
                        )
                    }
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        WeatherLocationHeader(mockWeatherData.locationName, textColor)
                        CurrentWeatherInfoCard(
                            mockWeatherData.current,
                            textColor,
                            cardColor,
                            cardTextColor
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Pronóstico (3 días)",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        forecastDays.forEach { forecastDay ->
                            WeatherForecastDayCard(forecastDay, cardColor, cardTextColor)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    item {
                        WeatherLocationHeader(mockWeatherData.locationName, textColor)
                    }

                    item {
                        CurrentWeatherInfoCard(
                            mockWeatherData.current,
                            textColor,
                            cardColor,
                            cardTextColor
                        )
                    }

                    item {
                        Text(
                            text = "Pronóstico (3 días)",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            modifier = Modifier.padding(
                                horizontal = 24.dp,
                                vertical = 16.dp
                            )
                        )
                    }

                    items(forecastDays) { forecastDay ->
                        WeatherForecastDayCard(forecastDay, cardColor, cardTextColor)
                    }
                }
            }
        }
    }
}
