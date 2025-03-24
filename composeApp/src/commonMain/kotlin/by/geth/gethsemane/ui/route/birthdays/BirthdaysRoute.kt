package by.geth.gethsemane.ui.route.birthdays

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import by.geth.gethsemane.ui.widget.BackNavigationTopAppBar
import gethsemane.composeapp.generated.resources.Res
import gethsemane.composeapp.generated.resources.birthdays
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BirthdaysRoute(
    navController: NavController,
    viewModel: BirthdaysViewModel = koinViewModel(),
) {
    Scaffold(
        topBar = {
            BackNavigationTopAppBar(
                title = stringResource(Res.string.birthdays),
                navController = navController,
            )
        },
    ) { contentPadding ->

    }
}
