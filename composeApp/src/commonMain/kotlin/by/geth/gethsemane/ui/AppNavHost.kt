package by.geth.gethsemane.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import by.geth.gethsemane.ui.route.birthdays.BirthdaysRoute
import by.geth.gethsemane.ui.route.home.HomeRoute
import by.geth.gethsemane.ui.route.init.InitRoute
import by.geth.gethsemane.ui.route.schedule.ScheduleRoute

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = InitRoute,
    ) {
        composable<InitRoute> {
            InitRoute(navController)
        }
        composable<HomeRoute> {
            HomeRoute(navController)
        }
        composable<ScheduleRoute> {
            ScheduleRoute(navController)
        }
        composable<BirthdaysRoute> {
            BirthdaysRoute(navController)
        }
    }
}
