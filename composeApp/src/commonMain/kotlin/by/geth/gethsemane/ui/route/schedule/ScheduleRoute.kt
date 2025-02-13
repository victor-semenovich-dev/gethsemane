package by.geth.gethsemane.ui.route.schedule

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import by.geth.gethsemane.ui.widget.CustomTopAppBar

@Composable
fun ScheduleRoute(
    navController: NavController,
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Расписание",
                navController = navController,
            )
        },
    ) {
    }
}
