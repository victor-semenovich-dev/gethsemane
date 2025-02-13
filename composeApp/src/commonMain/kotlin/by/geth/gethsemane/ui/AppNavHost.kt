package by.geth.gethsemane.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import by.geth.gethsemane.ui.route.main.MainRoute

const val MAIN_ROUTE = "main"

@Composable
fun AppNavHost() {
    NavHost(
        navController = rememberNavController(),
        startDestination = MAIN_ROUTE,
    ) {
        composable(route = MAIN_ROUTE) {
            MainRoute()
        }
    }
}
