package by.geth.gethsemane.ui.route.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import by.geth.gethsemane.ui.Schedule
import by.geth.gethsemane.ui.widget.CustomTopAppBar

@Composable
fun HomeRoute(
    navController: NavController,
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navController = navController,
                title = "Гефсимания",
                showBackButton = false,
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp),
        ) {
            Button(
                onClick = {
                    navController.navigate(Schedule)
                },
            ) {
                Text("Расписание")
            }
        }
    }
}
