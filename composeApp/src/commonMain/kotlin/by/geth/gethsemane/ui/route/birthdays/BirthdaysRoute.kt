package by.geth.gethsemane.ui.route.birthdays

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import by.geth.gethsemane.domain.model.Birthdays
import by.geth.gethsemane.domain.util.isToday
import by.geth.gethsemane.domain.util.isTomorrow
import by.geth.gethsemane.ui.util.ObserveAsEvents
import by.geth.gethsemane.ui.util.formattedDate
import by.geth.gethsemane.ui.widget.BackNavigationTopAppBar
import gethsemane.composeapp.generated.resources.Res
import gethsemane.composeapp.generated.resources.birthdays
import gethsemane.composeapp.generated.resources.failure_data_loading
import gethsemane.composeapp.generated.resources.today
import gethsemane.composeapp.generated.resources.tomorrow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdaysRoute(
    viewModel: BirthdaysViewModel = koinViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            BackNavigationTopAppBar(
                title = stringResource(Res.string.birthdays),
            )
        },
    ) { contentPadding ->
        PullToRefreshBox(
            modifier = Modifier.padding(
                top = contentPadding.calculateTopPadding(),
            ),
            isRefreshing = viewModel.uiState.isLoading,
            onRefresh = viewModel::loadData,
        ) {
            BirthdaysList(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = contentPadding.calculateEndPadding(LayoutDirection.Ltr),
                    bottom = contentPadding.calculateBottomPadding(),
                ),
                birthdaysList = viewModel.uiState.birthdays,
            )
        }
    }

    val errorMessage = stringResource(Res.string.failure_data_loading)
    ObserveAsEvents(viewModel.eventsFlow) { event ->
        when (event) {
            is BirthdaysEvent.ErrorEvent -> {
                snackbarHostState.showSnackbar(errorMessage)
            }
        }
    }

    LifecycleStartEffect(Unit) {
        viewModel.loadData()
        onStopOrDispose {}
    }
}

@Composable
fun BirthdaysList(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    birthdaysList: List<Birthdays>,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            bottom = contentPadding.calculateBottomPadding(),
        ),
    ) {
        items(birthdaysList) { item ->
            BirthdaysItem(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = contentPadding.calculateEndPadding(LayoutDirection.Ltr),
                ),
                item = item,
            )
        }
    }
}

@Composable
fun BirthdaysItem(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    item: Birthdays,
) {
    Column {
        Row(
            modifier = modifier.background(
                color = if (item.date.isToday()) MaterialTheme.colorScheme.surfaceContainerHighest
                    else if (item.date.isTomorrow()) MaterialTheme.colorScheme.surfaceContainer
                    else Color.Transparent,
            ).padding(contentPadding).padding(8.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.formattedDate)
                if (item.date.isToday()) {
                    Text(
                        text = stringResource(Res.string.today),
                        style = MaterialTheme.typography.labelMedium,
                    )
                } else if (item.date.isTomorrow()) {
                    Text(
                        text = stringResource(Res.string.tomorrow),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(2f)) {
                for (person in item.persons) {
                    Text(person)
                }
            }
        }
        HorizontalDivider()
    }
}
