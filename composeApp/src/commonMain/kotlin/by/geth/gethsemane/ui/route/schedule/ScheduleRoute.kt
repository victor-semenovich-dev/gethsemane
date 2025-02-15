package by.geth.gethsemane.ui.route.schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.NavController
import by.geth.gethsemane.domain.model.Event
import by.geth.gethsemane.ui.widget.CustomTopAppBar
import gethsemane.composeapp.generated.resources.Res
import gethsemane.composeapp.generated.resources.failure_data_loading
import gethsemane.composeapp.generated.resources.schedule
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ScheduleRoute(
    navController: NavController,
    viewModel: ScheduleViewModel = koinViewModel()
) {
    val scheduleUiState by viewModel.uiState.collectAsState()
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
            is ScheduleUiState.Failure -> {
                ScheduleFailure()
            }
            is ScheduleUiState.Success -> {
                ScheduleSuccess(
                    events = (scheduleUiState as ScheduleUiState.Success).events,
                    formatDateTime = viewModel::format,
                )
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
fun ScheduleSuccess(
    events: List<Event>,
    formatDateTime: (LocalDateTime) -> String,
) {
    LazyColumn(
        contentPadding = WindowInsets.navigationBars.asPaddingValues(),
    ) {
        items(events) { event ->
            ScheduleItem(event, formatDateTime)
        }
    }
}

@Composable
fun ScheduleItem(
    event: Event,
    formatDateTime: (LocalDateTime) -> String,
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = event.title)
        Text(text = formatDateTime(event.dateTime))
    }
}
