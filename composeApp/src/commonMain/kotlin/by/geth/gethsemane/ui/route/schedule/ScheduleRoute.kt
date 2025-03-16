package by.geth.gethsemane.ui.route.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import by.geth.gethsemane.domain.model.Schedule
import by.geth.gethsemane.ui.widget.BackNavigationTopAppBar
import gethsemane.composeapp.generated.resources.Res
import gethsemane.composeapp.generated.resources.failure_data_loading
import gethsemane.composeapp.generated.resources.schedule
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleRoute(
    navController: NavController,
    viewModel: ScheduleViewModel = koinViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            BackNavigationTopAppBar(
                title = stringResource(Res.string.schedule),
                navController = navController,
            )
        },
    ) { contentPadding ->
        PullToRefreshBox(
            modifier = Modifier.padding(
                top = contentPadding.calculateTopPadding(),
                start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = contentPadding.calculateEndPadding(LayoutDirection.Ltr),
            ),
            isRefreshing = viewModel.uiState.isLoading,
            onRefresh = viewModel::loadData,
        ) {
            ScheduleList(
                schedule = viewModel.uiState.schedule,
            )
        }
    }

    val errorMessage = stringResource(Res.string.failure_data_loading)
    LaunchedEffect(viewModel.uiState.error) {
        if (viewModel.uiState.error != null) {
            snackbarHostState.showSnackbar(errorMessage)
            viewModel.consumeError()
        }
    }
}

@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
fun ScheduleList(
    modifier: Modifier = Modifier,
    schedule: Schedule,
) {
    val dateTimeFormat = LocalDateTime.Format {
        byUnicodePattern("dd.MM.yyyy HH:mm")
    }
    LazyColumn(modifier = modifier) {
        items(schedule.items) { scheduleItem ->
            val dateTime = scheduleItem.dateTime.format(dateTimeFormat)
            val musicGroup = scheduleItem.musicGroup
            val subtitle = if (musicGroup != null) "$dateTime • $musicGroup" else dateTime
            ScheduleListItem(
                title = scheduleItem.title,
                subTitle = subtitle,
            )
        }
    }
}

@Composable
fun ScheduleListItem(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = title)
            Text(text = subTitle)
        }
        HorizontalDivider()
    }
}
