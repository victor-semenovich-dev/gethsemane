package by.geth.gethsemane.ui.route.schedule

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import by.geth.gethsemane.ui.widget.CustomTopAppBar
import gethsemane.composeapp.generated.resources.Res
import gethsemane.composeapp.generated.resources.schedule
import org.jetbrains.compose.resources.stringResource

@Composable
fun ScheduleRoute(
    navController: NavController,
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = stringResource(Res.string.schedule),
                navController = navController,
            )
        },
    ) {
    }
}
