package by.geth.gethsemane.ui.route.init

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import by.geth.gethsemane.ui.HomeRoute
import by.geth.gethsemane.ui.InitRoute
import gethsemane.composeapp.generated.resources.Res
import gethsemane.composeapp.generated.resources.failure_data_loading
import gethsemane.composeapp.generated.resources.retry
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun InitRoute(
    navController: NavController,
    viewModel: InitViewModel = koinViewModel(),
) {
    val isDataLoaded = viewModel.uiState.isDataLoaded
    LaunchedEffect(isDataLoaded) {
        if (isDataLoaded) {
            navController.navigate(HomeRoute) {
                popUpTo(InitRoute) { inclusive = true }
            }
        }
    }
    Scaffold { contentPadding ->
        Box(Modifier.padding(contentPadding).fillMaxSize()) {
            if (viewModel.uiState.isFailedToLoadData) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(stringResource(Res.string.failure_data_loading))
                    Button(onClick = viewModel::loadData) {
                        Text(stringResource(Res.string.retry))
                    }
                }
            }
            if (viewModel.uiState.isDataLoading) {
                CircularProgressIndicator(
                    Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
