package by.geth.gethsemane.ui.route.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.domain.manager.ScheduleManager
import by.geth.gethsemane.domain.model.Schedule
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val scheduleManager: ScheduleManager,
): ViewModel() {
    var uiState: ScheduleUiState by mutableStateOf(ScheduleUiState())
        private set

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            scheduleManager.scheduleFlow.collectLatest { schedule ->
                uiState = uiState.copy(schedule = schedule)
            }
        }
    }

    fun loadData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            scheduleManager.loadSchedule().onSuccess {
                uiState = uiState.copy(isLoading = false, error = null)
            }.onFailure { error ->
                uiState = uiState.copy(isLoading = false, error = error)
            }
        }
    }

    fun consumeError() {
        uiState = uiState.copy(error = null)
    }
}

data class ScheduleUiState(
    val schedule: Schedule = Schedule(items = emptyList()),
    val isLoading: Boolean = false,
    val error: Throwable? = null, // TODO use channels for one-time events
)
