package by.geth.gethsemane.ui.route.home.worshipList.worship

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleStartEffect
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun WorshipWidget(
    modifier: Modifier = Modifier,
    eventId: Int,
    viewModel: WorshipViewModel = koinViewModel { parametersOf(eventId) }
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = eventId.toString(),
            fontSize = 24.sp,
        )
        if (viewModel.uiState.isLoading) {
            Text("Loading...")
        }
        if (viewModel.uiState.isLoaded) {
            Text("Loaded")
        }
        if (viewModel.uiState.isError) {
            Text("An error occurred")
        }
    }

    LifecycleStartEffect(Unit) {
        viewModel.loadWorship()
        onStopOrDispose {}
    }
}
