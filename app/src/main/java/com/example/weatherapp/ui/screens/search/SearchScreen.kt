package com.example.weatherapp.ui.screens.search

import androidx.activity.compose.BackHandler
import android.app.Activity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.data.model.Location
import com.example.weatherapp.ui.theme.GradientColorsDark
import com.example.weatherapp.ui.theme.GradientColorsLight
import com.example.weatherapp.ui.theme.IconBlue
import com.example.weatherapp.ui.theme.IconBlueLight
import com.example.weatherapp.ui.theme.IconOrange
import com.example.weatherapp.ui.theme.IconOrangeDark
import com.example.weatherapp.ui.theme.IconOrangeLight
import com.example.weatherapp.ui.theme.getCardColor
import com.example.weatherapp.ui.theme.getCardIconColor
import com.example.weatherapp.ui.theme.getCardTextColor
import com.example.weatherapp.ui.theme.getDialogColor
import com.example.weatherapp.ui.theme.getSearchFieldBackgroundColor
import com.example.weatherapp.ui.theme.getSearchFieldIconColor
import com.example.weatherapp.ui.theme.getSearchFieldTextColor
import com.example.weatherapp.ui.theme.getTextColor
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.ui.viewmodel.SearchViewModel
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.delay

private enum class WeatherIconType(val iconResId: Int) {
    CLEAR_DAY(R.drawable.ic_sun_clear_day),
    PARTLY_CLOUDY(R.drawable.ic_partly_cloudy),
    THUNDERSTORM(R.drawable.ic_thunderstorm)
}

@Composable
private fun getGradientColors(): List<Color> {
    return if (isSystemInDarkTheme()) {
        GradientColorsDark
    } else {
        GradientColorsLight
    }
}

/**
 * Search screen for finding locations.
 * Displays search input and results list with pull-to-refresh functionality.
 *
 * @param onNavigateToDetails Callback to navigate to weather details screen
 * @param viewModel ViewModel instance (injected via Koin)
 */
@Composable
fun SearchScreen(
    onNavigateToDetails: (String) -> Unit,
    viewModel: SearchViewModel = koinViewModel()
) {
            val uiState by viewModel.uiState.collectAsState()
            val gradientColors = getGradientColors()
            val textColor = getTextColor()
            val cardColor = getCardColor()
            val cardTextColor = getCardTextColor()
            val cardIconColor = getCardIconColor()
            val context = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }
    
    var pullOffset by remember { mutableFloatStateOf(0f) }
    var isRefreshing by remember { mutableStateOf(false) }
    
    BackHandler(enabled = true) {
        if (uiState.searchQuery.isNotBlank()) {
            viewModel.onSearchQueryChanged("")
    } else {
            showExitDialog = true
        }
    }
    
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            isRefreshing = false
        }
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
                                        viewModel.refreshLocationSearch()
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = viewModel::onSearchQueryChanged,
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
                                if (uiState.searchQuery.isNotBlank()) {
                            IconButton(
                                        onClick = { viewModel.onSearchQueryChanged("") }
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
                                                .padding(horizontal = 24.dp, vertical = 32.dp),
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

                            uiState.locations.isEmpty() && uiState.searchQuery.isNotBlank() -> {
                                val scrollState = rememberScrollState()
                                
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 80.dp),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .verticalScroll(scrollState)
                                            .padding(32.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(64.dp)
                                                .background(
                                                    brush = Brush.radialGradient(
                                                        colors = listOf(
                                                        IconOrange.copy(alpha = 0.2f),
                                                        IconOrangeDark.copy(alpha = 0.1f)
                                                        )
                                                    ),
                                                    shape = RoundedCornerShape(32.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = "Sin resultados",
                                                            tint = if (isSystemInDarkTheme()) {
                                                                IconOrangeLight
                                                            } else {
                                                                IconOrange
                                                            },
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                        Text(
                                            text = "No se encontraron ubicaciones",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor,
                                            modifier = Modifier.padding(top = 16.dp),
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "Intenta con otro nombre",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = textColor.copy(alpha = 0.7f),
                                            modifier = Modifier.padding(top = 8.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            uiState.locations.isEmpty() -> {
                                val scrollState = rememberScrollState()
                                
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(scrollState)
                                ) {
                                    Spacer(modifier = Modifier.padding(top = 16.dp))
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
                                                                GradientColorsLight.first().copy(alpha = 0.2f),
                                                                GradientColorsLight.last().copy(alpha = 0.1f)
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
                                                                        IconBlueLight
                                                    } else {
                                                                        IconBlue
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
                                    Spacer(modifier = Modifier.padding(bottom = 32.dp))
                                }
                            }

                            else -> {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    items(uiState.locations) { location ->
                                        LocationSearchResultCard(
                                            location = location,
                                            onClick = {
                                                onNavigateToDetails(location.name)
                                            },
                                            cardColor = cardColor,
                                            textColor = cardTextColor,
                                            iconColor = cardIconColor
                                        )
                                    }
                                }
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
        
        if (showExitDialog) {
            ExitApplicationConfirmationDialog(
                onConfirm = {
                    showExitDialog = false
                    (context as? Activity)?.finish()
                },
                onDismiss = {
                    showExitDialog = false
                },
                textColor = textColor,
                cardColor = getDialogColor(),
                cardTextColor = cardTextColor
            )
        }
    }
}

@Composable
private fun ExitApplicationConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    textColor: Color,
    cardColor: Color,
    cardTextColor: Color
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Salir de la aplicación",
                color = cardTextColor,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que deseas salir?",
                color = cardTextColor
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Salir",
                    color = if (isSystemInDarkTheme()) {
                        IconOrangeLight
                    } else {
                        IconOrangeDark
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    color = cardTextColor
                )
            }
        },
        containerColor = cardColor,
        shape = RoundedCornerShape(20.dp)
    )
}

/**
 * Animated weather icon component for the toolbar.
 * Cycles through different weather icons with rotation animation.
 *
 * @param modifier Modifier for the icon
 * @param iconColor Color for the icon
 */
@Composable
private fun AnimatedWeatherIconInToolbar(
    modifier: Modifier = Modifier,
    iconColor: Color = Color.White
) {
    var currentIconIndex by remember { mutableIntStateOf(0) }
    val weatherIcons = WeatherIconType.values()
    
    val sunRotation by rememberInfiniteTransition(label = "sunRotation").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sunRotation"
    )
    
    LaunchedEffect(Unit) {
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

/**
 * Card displaying a location search result.
 *
 * @param location Location data to display
 * @param onClick Callback when card is clicked
 * @param cardColor Background color for the card
 * @param textColor Text color
 * @param iconColor Icon color
 */
@Composable
fun LocationSearchResultCard(
    location: Location,
    onClick: () -> Unit,
    cardColor: Color,
    textColor: Color,
    iconColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Ubicación",
                                                            tint = if (isSystemInDarkTheme()) {
                                                                IconBlueLight
                                                            } else {
                                                                IconBlue
                                                            },
                modifier = Modifier
                    .size(35.dp)
                    .padding(end = 12.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = location.country,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Resultado de búsqueda",
                tint = iconColor,
                modifier = Modifier
                    .size(35.dp)
                    .padding(start = 8.dp)
            )
        }
    }
}

@Preview(
    name = "Search Screen - Empty State Light",
    showBackground = true,
    backgroundColor = 0xFF3B82F6,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun SearchScreenEmptyLightPreview() {
    WeatherAppTheme(darkTheme = false) {
        SearchScreenEmptyContent()
    }
}

@Preview(
    name = "Search Screen - Empty State Dark",
    showBackground = true,
    backgroundColor = 0xFF0F172A,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun SearchScreenEmptyDarkPreview() {
    WeatherAppTheme(darkTheme = true) {
        SearchScreenEmptyContent()
    }
}

@Composable
private fun SearchScreenEmptyContent() {
    val gradientColors = getGradientColors()
    val textColor = getTextColor()
    val cardColor = getCardColor()
    val cardTextColor = getCardTextColor()
    val cardIconColor = getCardIconColor()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = gradientColors))
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
                    value = "",
                    onValueChange = {},
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
                            trailingIcon = null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = getSearchFieldTextColor(),
                                unfocusedTextColor = getSearchFieldTextColor(),
                                focusedContainerColor = getSearchFieldBackgroundColor(),
                                unfocusedContainerColor = getSearchFieldBackgroundColor(),
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedLeadingIconColor = getSearchFieldIconColor(),
                                unfocusedLeadingIconColor = getSearchFieldIconColor()
                            ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

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
                                                GradientColorsLight.first().copy(alpha = 0.2f),
                                                GradientColorsLight.last().copy(alpha = 0.1f)
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
                                                                IconBlueLight
                                                            } else {
                                                                IconBlue
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
        }
    }
}

@Preview(
    name = "Search Screen - With Results Light",
    showBackground = true,
    backgroundColor = 0xFF3B82F6,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun SearchScreenWithResultsLightPreview() {
    WeatherAppTheme(darkTheme = false) {
        SearchScreenWithResultsContent()
    }
}

@Preview(
    name = "Search Screen - With Results Dark",
    showBackground = true,
    backgroundColor = 0xFF0F172A,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun SearchScreenWithResultsDarkPreview() {
    WeatherAppTheme(darkTheme = true) {
        SearchScreenWithResultsContent()
    }
}

@Composable
private fun SearchScreenWithResultsContent() {
    val gradientColors = getGradientColors()
    val textColor = getTextColor()
    val cardColor = getCardColor()
    val cardTextColor = getCardTextColor()
    val cardIconColor = getCardIconColor()

    val mockLocations = listOf(
        Location(id = 1, name = "Bogotá", region = "Cundinamarca", country = "Colombia", lat = 4.6097, lon = -74.0817, url = ""),
        Location(id = 2, name = "Medellín", region = "Antioquia", country = "Colombia", lat = 6.2476, lon = -75.5658, url = ""),
        Location(id = 3, name = "Cali", region = "Valle del Cauca", country = "Colombia", lat = 3.4516, lon = -76.5320, url = "")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = gradientColors))
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
                    value = "Bogotá",
                    onValueChange = {},
                    placeholder = {
                        Text(
                            text = "Buscar ciudad...",
                            color = getCardIconColor().copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = getCardIconColor(),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    trailingIcon = null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = getCardTextColor(),
                        unfocusedTextColor = getCardTextColor(),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLeadingIconColor = getCardIconColor(),
                        unfocusedLeadingIconColor = getCardIconColor()
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    item {
                        Text(
                            text = "Pronóstico",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }

                    items(mockLocations) { location ->
                        LocationSearchResultCard(
                            location = location,
                            onClick = {},
                            cardColor = cardColor,
                            textColor = cardTextColor,
                            iconColor = cardIconColor
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Search Screen - Loading Light",
    showBackground = true,
    backgroundColor = 0xFF3B82F6,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun SearchScreenLoadingLightPreview() {
    WeatherAppTheme(darkTheme = false) {
        SearchScreenLoadingContent()
    }
}

@Preview(
    name = "Search Screen - Loading Dark",
    showBackground = true,
    backgroundColor = 0xFF0F172A,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun SearchScreenLoadingDarkPreview() {
    WeatherAppTheme(darkTheme = true) {
        SearchScreenLoadingContent()
    }
}

@Composable
private fun SearchScreenLoadingContent() {
    val gradientColors = getGradientColors()
    val textColor = getTextColor()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = gradientColors))
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
                    value = "Bogotá",
                    onValueChange = {},
                    placeholder = {
                        Text(
                            text = "Buscar ciudad...",
                            color = Color.Gray.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = textColor.copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    trailingIcon = null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = textColor)
                }
            }
        }
    }
}
