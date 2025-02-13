package by.geth.gethsemane.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import by.geth.gethsemane.ui.route.home.HomeRoute
import by.geth.gethsemane.ui.route.schedule.ScheduleRoute

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Home,
    ) {
        composable<Home> {
            HomeRoute(navController)
        }
        composable<Schedule> {
            ScheduleRoute(navController)
        }
    }
}
