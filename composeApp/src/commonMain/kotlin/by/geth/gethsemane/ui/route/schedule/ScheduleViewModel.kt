package by.geth.gethsemane.ui.route.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.domain.model.Schedule
import by.geth.gethsemane.domain.usecase.LoadScheduleUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val loadSchedule: LoadScheduleUseCase,
): ViewModel() {
    private val eventsChannel = Channel<ScheduleEvent>()
    val eventsFlow = eventsChannel.receiveAsFlow()

    var uiState: ScheduleUiState by mutableStateOf(ScheduleUiState())
        private set

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            loadSchedule.dataFlow.collectLatest { schedule ->
                uiState = uiState.copy(schedule = schedule)
            }
        }
    }

    fun loadData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            loadSchedule().onSuccess {
                uiState = uiState.copy(isLoading = false)
            }.onFailure { error ->
                uiState = uiState.copy(isLoading = false)
                eventsChannel.send(ScheduleEvent.ErrorEvent(error))
            }
        }
    }
}

data class ScheduleUiState(
    val schedule: Schedule = Schedule(items = emptyList()),
    val isLoading: Boolean = false,
)

sealed interface ScheduleEvent {
    data class ErrorEvent(val error: Throwable): ScheduleEvent
}
