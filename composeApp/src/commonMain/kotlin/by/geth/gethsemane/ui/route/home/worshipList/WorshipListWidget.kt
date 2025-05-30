package by.geth.gethsemane.ui.route.home.worshipList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleStartEffect
import by.geth.gethsemane.ui.LocalSnackbarHostState
import by.geth.gethsemane.ui.route.home.HomeViewModel
import by.geth.gethsemane.ui.route.home.worshipList.worship.WorshipWidget
import by.geth.gethsemane.ui.util.ObserveAsEvents
import gethsemane.composeapp.generated.resources.Res
import gethsemane.composeapp.generated.resources.failure_worship_list_loading
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WorshipListWidget(
    modifier: Modifier = Modifier,
    viewModel: WorshipListViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel(),
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val pagerState = rememberPagerState(pageCount = {
        viewModel.uiState.worshipEvents.size
    })

    LaunchedEffect(pagerState, viewModel.uiState.worshipEvents) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged() // Only emit when the page actually changes
            .collect { page ->
                // This code can be called while events is not loaded yet (worshipEvents is empty)
                viewModel.uiState.worshipEvents.getOrNull(page)?.let { event ->
                    homeViewModel.updateCurrentEvent(event)
                }
            }
    }

    HorizontalPager(
        modifier = modifier.fillMaxSize(),
        state = pagerState,
    ) { page ->
        WorshipWidget(eventId = viewModel.uiState.worshipEvents[page].id)
    }

    if (viewModel.uiState.worshipEvents.isEmpty() && viewModel.uiState.isLoading) {
        Box(modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    }

    val errorMessage = stringResource(Res.string.failure_worship_list_loading)
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
