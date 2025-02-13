package by.geth.gethsemane.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import by.geth.gethsemane.ui.route.home.HomeRoute

@Composable
fun AppNavHost() {
    NavHost(
        navController = rememberNavController(),
        startDestination = Home,
    ) {
        composable<Home> {
            HomeRoute()
        }
    }
}
