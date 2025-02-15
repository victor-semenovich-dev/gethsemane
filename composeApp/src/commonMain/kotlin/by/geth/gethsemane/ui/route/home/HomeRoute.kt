package by.geth.gethsemane.ui.route.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import by.geth.gethsemane.ui.Schedule
import by.geth.gethsemane.ui.widget.CustomTopAppBar
import gethsemane.composeapp.generated.resources.Res
import gethsemane.composeapp.generated.resources.gethsemane
import gethsemane.composeapp.generated.resources.schedule
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeRoute(
    navController: NavController,
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navController = navController,
                title = stringResource(Res.string.gethsemane),
                showBackButton = false,
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier.padding(contentPadding).padding(horizontal = 8.dp),
        ) {
            Button(
                onClick = {
                    navController.navigate(Schedule)
                },
            ) {
                Text(stringResource(Res.string.schedule))
            }
        }
    }
}
