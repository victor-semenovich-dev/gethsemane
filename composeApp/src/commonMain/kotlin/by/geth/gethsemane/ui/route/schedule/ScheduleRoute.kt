package by.geth.gethsemane.ui.route.schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import by.geth.gethsemane.domain.model.Schedule
import by.geth.gethsemane.domain.model.ScheduleItem
import gethsemane.composeapp.generated.resources.Res
import gethsemane.composeapp.generated.resources.back
import gethsemane.composeapp.generated.resources.failure_data_loading
import gethsemane.composeapp.generated.resources.schedule
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleRoute(
    navController: NavController,
    viewModel: ScheduleViewModel = koinViewModel()
) {
    val scheduleUiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back),
                        )
                    }
                },
                title = {
                    Text(text = stringResource(Res.string.schedule))
                },
            )
        },
    ) { contentPadding ->
        when (scheduleUiState) {
            ScheduleUiState.None -> {}
            ScheduleUiState.Loading -> {
                ScheduleLoading(modifier = Modifier.padding(contentPadding))
            }
            is ScheduleUiState.Failure -> {
                ScheduleFailure(modifier = Modifier.padding(contentPadding))
            }
            is ScheduleUiState.Success -> {
                ScheduleSuccess(
                    modifier = Modifier.padding(
                        top = contentPadding.calculateTopPadding(),
                        start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = contentPadding.calculateEndPadding(LayoutDirection.Rtl),
                    ),
                    schedule = (scheduleUiState as ScheduleUiState.Success).schedule,
                    buildSubtitle = viewModel::buildSubtitle,
                )
            }
        }
    }
}

@Composable
fun ScheduleLoading(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize()) {
        CircularProgressIndicator(Modifier.align(Alignment.Center))
    }
}

@Composable
fun ScheduleFailure(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.align(Alignment.Center).padding(8.dp),
            text = stringResource(Res.string.failure_data_loading),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
fun ScheduleSuccess(
    modifier: Modifier = Modifier,
    schedule: Schedule,
    buildSubtitle: (ScheduleItem) -> String,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = WindowInsets.navigationBars.asPaddingValues(),
    ) {
        items(schedule.items) { scheduleItem ->
            ScheduleItem(
                title = scheduleItem.title,
                subTitle = buildSubtitle(scheduleItem)
            )
        }
    }
}

@Composable
fun ScheduleItem(
    title: String,
    subTitle: String,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = title)
            Text(text = subTitle)
        }
        HorizontalDivider()
    }
}
