package by.geth.gethsemane.ui.route.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.domain.model.Event
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
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = ScheduleUiState.Loading
            eventsRepository.loadEvents().onSuccess { events ->
                _uiState.value = ScheduleUiState.Success(events)
            }.onFailure { reason ->
                _uiState.value = ScheduleUiState.Failure(reason)
            }
        }
    }
}

sealed class ScheduleUiState {
    data object None: ScheduleUiState()
    data object Loading: ScheduleUiState()
    data class Success(
        val events: List<Event>,
    ): ScheduleUiState()
    data class Failure(
        val reason: Throwable,
    ): ScheduleUiState()
}
