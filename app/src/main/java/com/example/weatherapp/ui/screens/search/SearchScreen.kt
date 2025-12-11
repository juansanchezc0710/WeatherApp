package com.example.weatherapp.ui.screens.search

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.ui.viewmodel.SearchUiState
import com.example.weatherapp.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.delay

private enum class WeatherIconType(val iconResId: Int) {
    CLEAR_DAY(R.drawable.ic_sun_clear_day),
    PARTLY_CLOUDY(R.drawable.ic_partly_cloudy),
    THUNDERSTORM(R.drawable.ic_thunderstorm)
}

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
private fun getSearchFieldBackgroundColor(): Color {
    return if (isSystemInDarkTheme()) {
        Color(0xFF1E3A8A).copy(alpha = 0.8f)
    } else {
        Color.White
    }
}

@Composable
private fun getSearchFieldTextColor(): Color {
    return if (isSystemInDarkTheme()) {
        Color(0xFFFFFFFF)
    } else {
        Color(0xFF212121)
    }
}

@Composable
private fun getSearchFieldIconColor(): Color {
    return if (isSystemInDarkTheme()) {
        Color(0xFFFFFFFF).copy(alpha = 0.9f)
    } else {
        Color(0xFF757575)
    }
}

@Composable
fun SearchScreen(viewModel: SearchViewModel = koinViewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val gradientColors = getGradientColors()
    val textColor = getTextColor()
    val cardColor = getCardColor()
    val cardTextColor = getCardTextColor()

    androidx.compose.runtime.LaunchedEffect(searchQuery) {
        viewModel.searchLocations(searchQuery)
    }

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
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Buscar Ubicación",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    AnimatedWeatherIconInToolbar(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(start = 8.dp),
                        iconColor = textColor
                    )
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Buscar ciudad...",
                            color = getSearchFieldIconColor().copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = getSearchFieldIconColor(),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(
                                onClick = { 
                                    searchQuery = ""
                                    viewModel.clearState()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Limpiar",
                                    tint = getSearchFieldIconColor(),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = getSearchFieldTextColor(),
                        unfocusedTextColor = getSearchFieldTextColor(),
                        focusedContainerColor = getSearchFieldBackgroundColor(),
                        unfocusedContainerColor = getSearchFieldBackgroundColor(),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLeadingIconColor = getSearchFieldIconColor(),
                        unfocusedLeadingIconColor = getSearchFieldIconColor(),
                        focusedTrailingIconColor = getSearchFieldIconColor(),
                        unfocusedTrailingIconColor = getSearchFieldIconColor()
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                when (val currentState = uiState) {
                    is SearchUiState.Empty -> {
                        EmptyStateCard(
                            cardColor = cardColor,
                            cardTextColor = cardTextColor
                        )
                    }
                    is SearchUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Buscando...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = textColor
                            )
                        }
                    }
                    is SearchUiState.LocationsLoaded -> {
                        LocationsList(
                            locations = currentState.locations,
                            textColor = textColor,
                            cardColor = cardColor,
                            cardTextColor = cardTextColor
                        )
                    }
                    is SearchUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentState.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    cardColor: Color,
    cardTextColor: Color
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardColor
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF3B82F6).copy(alpha = 0.2f),
                                    Color(0xFF1E40AF).copy(alpha = 0.1f)
                                )
                            ),
                            shape = RoundedCornerShape(40.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Ubicación",
                        tint = if (isSystemInDarkTheme()) {
                            Color(0xFF64B5F6)
                        } else {
                            Color(0xFF2196F3)
                        },
                        modifier = Modifier.size(48.dp)
                    )
                }
                Text(
                    text = "Escribe el nombre de una ciudad para comenzar",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = cardTextColor,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Busca cualquier ciudad del mundo para ver su pronóstico del clima",
                    style = MaterialTheme.typography.bodyMedium,
                    color = cardTextColor.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun LocationsList(
    locations: List<com.example.weatherapp.data.model.Location>,
    textColor: Color,
    cardColor: Color,
    cardTextColor: Color
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
    ) {
        items(locations) { location ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { /* TODO: Navegar a detalles */ },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardColor
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = if (isSystemInDarkTheme()) {
                            Color(0xFF64B5F6)
                        } else {
                            Color(0xFF2196F3)
                        },
                        modifier = Modifier.size(24.dp)
                    )
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = location.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = cardTextColor
                        )
                        Text(
                            text = "${location.region}, ${location.country}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = cardTextColor.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedWeatherIconInToolbar(
    modifier: Modifier = Modifier,
    iconColor: Color = Color.White
) {
    var currentIconIndex by remember { mutableIntStateOf(0) }
    val weatherIcons = WeatherIconType.entries
    
    val sunRotation by rememberInfiniteTransition(label = "sunRotation").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sunRotation"
    )
    
    androidx.compose.runtime.LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            currentIconIndex = (currentIconIndex + 1) % weatherIcons.size
        }
    }
    
    val currentIcon = weatherIcons[currentIconIndex]
    val shouldRotate = currentIcon == WeatherIconType.CLEAR_DAY
    
    Icon(
        painter = painterResource(id = currentIcon.iconResId),
        contentDescription = "Weather Icon",
        modifier = modifier
            .then(
                if (shouldRotate) {
                    Modifier.rotate(sunRotation)
                } else {
                    Modifier
                }
            ),
        tint = iconColor
    )
}

@Preview(
    name = "Search Screen Light",
    showBackground = true,
    backgroundColor = 0xFF3B82F6,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun SearchScreenLightPreview() {
    SearchScreen()
}

@Preview(
    name = "Search Screen Dark",
    showBackground = true,
    backgroundColor = 0xFF0F172A,
    showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun SearchScreenDarkPreview() {
    SearchScreen()
}
