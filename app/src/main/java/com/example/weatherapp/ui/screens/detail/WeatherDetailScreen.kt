package com.example.weatherapp.ui.screens.detail

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weatherapp.R
import com.example.weatherapp.domain.model.CurrentWeather
import com.example.weatherapp.domain.model.ForecastDay
import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.ui.theme.WeatherAppTheme

private val GRADIENT_COLORS_LIGHT = listOf(
    Color(0xFF3B82F6),
    Color(0xFF1E40AF)
)

private val GRADIENT_COLORS_DARK = listOf(
    Color(0xFF1E3A8A),
    Color(0xFF0F172A)
)

@Composable
private fun getGradientColors(): List<Color> {
    return if (isSystemInDarkTheme()) {
        GRADIENT_COLORS_DARK
    } else {
        GRADIENT_COLORS_LIGHT
    }
}

@Composable
private fun getTextColor(): Color {
    return if (isSystemInDarkTheme()) {
        Color(0xFFE0E0E0)
    } else {
        Color.White
    }
}

@Composable
private fun getCardColor(): Color {
    return if (isSystemInDarkTheme()) {
        Color(0xFF1E3A8A).copy(alpha = 0.6f)
    } else {
        Color.White.copy(alpha = 0.9f)
    }
}

@Composable
private fun getCardTextColor(): Color {
    return if (isSystemInDarkTheme()) {
        Color(0xFFE0E0E0)
    } else {
        Color(0xFF212121)
    }
}

@Composable
private fun getCardIconColor(): Color {
    return if (isSystemInDarkTheme()) {
        Color(0xFFE0E0E0).copy(alpha = 0.7f)
    } else {
        Color(0xFF757575)
    }
}

/**
 * Weather detail screen showing current weather and forecast.
 *
 * @param weather Weather data to display
 * @param onBackClick Callback when back button is clicked
 */
@Composable
fun WeatherDetailScreen(
    weather: Weather,
    onBackClick: () -> Unit
) {
    val gradientColors = getGradientColors()
    val textColor = getTextColor()
    val cardColor = getCardColor()
    val cardTextColor = getCardTextColor()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp
    val forecastDays = weather.forecast.take(3)

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
                        WeatherLocationHeader(weather.locationName, textColor)
                        CurrentWeatherInfoCard(weather.current, textColor, cardColor, cardTextColor)
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
                        WeatherLocationHeader(weather.locationName, textColor)
                    }

                    item {
                        CurrentWeatherInfoCard(weather.current, textColor, cardColor, cardTextColor)
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

@Composable
private fun WeatherLocationHeader(
    locationName: String,
    textColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = locationName,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

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
            .padding(horizontal = 24.dp, vertical = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = getCardColor()
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hoy",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = getCardTextColor(),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            AsyncImage(
                model = "https:${current.conditionIcon}",
                contentDescription = current.condition,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${current.temperature.toInt()}°C",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = getCardTextColor()
            )

            Text(
                text = current.condition,
                style = MaterialTheme.typography.titleMedium,
                color = getCardTextColor().copy(alpha = 0.8f),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
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
                Color(0xFFFF6B4A)
            } else {
                Color(0xFFFF5722)
            }
        }

        WeatherDetailIconType.WATER_DROP -> {
            if (isSystemInDarkTheme()) {
                Color(0xFF64B5F6)
            } else {
                Color(0xFF2196F3)
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
            ),
            ForecastDay(
                date = "2024-01-16",
                maxTemp = 22.0,
                minTemp = 14.0,
                avgTemp = 18.0,
                condition = "Soleado",
                conditionIcon = "//cdn.weatherapi.com/weather/64x64/day/113.png",
                avgHumidity = 60
            ),
            ForecastDay(
                date = "2024-01-17",
                maxTemp = 19.0,
                minTemp = 11.0,
                avgTemp = 15.0,
                condition = "Nublado",
                conditionIcon = "//cdn.weatherapi.com/weather/64x64/day/119.png",
                avgHumidity = 75
            )
        )
    )

    WeatherAppTheme(darkTheme = false) {
        WeatherDetailScreen(
            weather = mockWeatherData,
            onBackClick = {}
        )
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
            ),
            ForecastDay(
                date = "2024-01-16",
                maxTemp = 22.0,
                minTemp = 14.0,
                avgTemp = 18.0,
                condition = "Soleado",
                conditionIcon = "//cdn.weatherapi.com/weather/64x64/day/113.png",
                avgHumidity = 60
            ),
            ForecastDay(
                date = "2024-01-17",
                maxTemp = 19.0,
                minTemp = 11.0,
                avgTemp = 15.0,
                condition = "Nublado",
                conditionIcon = "//cdn.weatherapi.com/weather/64x64/day/119.png",
                avgHumidity = 75
            )
        )
    )

    WeatherAppTheme(darkTheme = true) {
        WeatherDetailScreen(
            weather = mockWeatherData,
            onBackClick = {}
        )
    }
}
