package by.geth.gethsemane.ui.route.schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import by.geth.gethsemane.ui.widget.CustomTopAppBar
import gethsemane.composeapp.generated.resources.Res
import gethsemane.composeapp.generated.resources.failure_data_loading
import gethsemane.composeapp.generated.resources.schedule
import org.jetbrains.compose.resources.stringResource

@Composable
fun ScheduleRoute(
    navController: NavController,
    scheduleViewModel: ScheduleViewModel = viewModel { ScheduleViewModel() }
) {
    val scheduleUiState by scheduleViewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = stringResource(Res.string.schedule),
                navController = navController,
            )
        },
    ) {
        when (scheduleUiState) {
            ScheduleUiState.None -> {}
            ScheduleUiState.Loading -> {
                ScheduleLoading()
            }
            ScheduleUiState.Failure -> {
                ScheduleFailure()
            }
            ScheduleUiState.Success -> {
                ScheduleSuccess()
            }
        }
    }
}

@Composable
fun ScheduleLoading() {
    Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator(Modifier.align(Alignment.Center))
    }
}

@Composable
fun ScheduleFailure() {
    Box(Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.align(Alignment.Center).padding(8.dp),
            text = stringResource(Res.string.failure_data_loading),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.error,
        )
    }
}

@Composable
fun ScheduleSuccess() {
    Box(Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.align(Alignment.Center).padding(8.dp),
            text = "Data loaded successfully",
            textAlign = TextAlign.Center,
        )
    }
}