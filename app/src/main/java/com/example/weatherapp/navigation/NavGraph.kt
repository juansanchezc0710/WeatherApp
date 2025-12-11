package com.example.weatherapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.weatherapp.ui.screens.detail.WeatherDetailScreen
import com.example.weatherapp.ui.screens.search.SearchScreen
import com.example.weatherapp.ui.screens.splash.SplashScreen

/**
 * Sealed class representing all screens in the app.
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Search : Screen("search")
    object Details : Screen("details/{locationName}") {
        fun createRoute(locationName: String) = "details/$locationName"
    }
}

/**
 * Navigation graph for the WeatherApp.
 * Handles navigation between Splash, Search, and Details screens.
 *
 * @param navController NavHostController for navigation
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateToDetails = { locationName ->
                    navController.navigate(Screen.Details.createRoute(locationName))
                }
            )
        }
        
        composable(
            route = Screen.Details.route,
            arguments = listOf(
                navArgument("locationName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val locationName = backStackEntry.arguments?.getString("locationName") ?: ""
            WeatherDetailScreen(
                locationName = locationName,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
