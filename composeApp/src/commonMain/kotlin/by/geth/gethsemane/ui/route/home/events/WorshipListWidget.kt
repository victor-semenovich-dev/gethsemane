package by.geth.gethsemane.ui.route.home.events

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleStartEffect
import by.geth.gethsemane.ui.util.ObserveAsEvents
import gethsemane.composeapp.generated.resources.Res
import gethsemane.composeapp.generated.resources.failure_data_loading
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WorshipListWidget(
    modifier: Modifier = Modifier,
    viewModel: WorshipListViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState,
) {
    Box(modifier.fillMaxSize()) {
        if (viewModel.uiState.worshipEvents.isEmpty() && viewModel.uiState.isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    }

    val errorMessage = stringResource(Res.string.failure_data_loading)
    ObserveAsEvents(viewModel.eventsFlow) { event ->
        when (event) {
            is WorshipListEvent.ErrorEvent -> {
                snackbarHostState.showSnackbar(errorMessage)
            }
        }
    }

    LifecycleStartEffect(Unit) {
        viewModel.loadData()
        onStopOrDispose {}
    }
}
