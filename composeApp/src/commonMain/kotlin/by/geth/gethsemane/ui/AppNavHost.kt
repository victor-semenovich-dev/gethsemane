package by.geth.gethsemane.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import by.geth.gethsemane.ui.route.birthdays.BirthdaysRoute
import by.geth.gethsemane.ui.route.home.HomeRoute
import by.geth.gethsemane.ui.route.schedule.ScheduleRoute

val LocalNavController = compositionLocalOf<NavHostController> { error("No nav controller provided") }
val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> { error("No snackbar host state provided") }

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
        ) {
            composable<HomeRoute> {
                HomeRoute()
            }
            composable<ScheduleRoute> {
                ScheduleRoute()
            }
            composable<BirthdaysRoute> {
                BirthdaysRoute()
            }
        }
    }
}
