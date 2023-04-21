package org.prikic.flightsearch.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.prikic.flightsearch.ui.destinations.DestinationsScreen
import org.prikic.flightsearch.ui.destinations.FlightDestinations
import org.prikic.flightsearch.ui.home.HomeDestination
import org.prikic.flightsearch.ui.home.HomeScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun FlightNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToAirportDestinations = { navController.navigate("${FlightDestinations.route}/${it.id}") }
            )
        }

        composable(
            route = FlightDestinations.routeWithArgs,
            arguments = listOf(navArgument(FlightDestinations.flightIdArg) {
                type = NavType.IntType
            })
        ) {
            DestinationsScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}
