package by.geth.gethsemane.ui.route.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.domain.repository.EventsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val eventsRepository: EventsRepository,
): ViewModel() {
    private val _uiState: MutableStateFlow<ScheduleUiState> = MutableStateFlow(ScheduleUiState.None)
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    init {
        _loadData()
    }

    private fun _loadData() {
        viewModelScope.launch {
            _uiState.value = ScheduleUiState.Loading
            eventsRepository.loadEvents()
            _uiState.value = ScheduleUiState.Success
        }
    }
}

sealed class ScheduleUiState {
    data object None: ScheduleUiState()
    data object Loading: ScheduleUiState()
    data object Success: ScheduleUiState()
    data object Failure: ScheduleUiState()
}
