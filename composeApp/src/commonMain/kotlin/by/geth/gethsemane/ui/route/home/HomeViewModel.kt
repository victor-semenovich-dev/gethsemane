package by.geth.gethsemane.ui.route.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.domain.model.Event
import by.geth.gethsemane.domain.usecase.LoadInitialDataUseCase
import kotlinx.coroutines.launch

class HomeViewModel(
    loadInitialData: LoadInitialDataUseCase,
): ViewModel() {
    var uiState by mutableStateOf(HomeState())
        private set

    init {
        viewModelScope.launch {
            loadInitialData()
            uiState = uiState.copy(initialDataLoaded = true)
        }
    }

    fun updateCurrentEvent(event: Event) {
        uiState = uiState.copy(currentEvent = event)
    }
}

data class HomeState(
    val currentEvent: Event? = null,
    val initialDataLoaded: Boolean = false,
)
