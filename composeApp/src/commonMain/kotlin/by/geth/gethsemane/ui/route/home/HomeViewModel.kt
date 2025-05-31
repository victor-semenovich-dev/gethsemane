package by.geth.gethsemane.ui.route.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import by.geth.gethsemane.domain.model.Event

class HomeViewModel: ViewModel() {
    var uiState by mutableStateOf(HomeState())
        private set

    fun updateCurrentEvent(event: Event) {
        uiState = uiState.copy(currentEvent = event)
    }
}

data class HomeState(
    val currentEvent: Event? = null,
)
