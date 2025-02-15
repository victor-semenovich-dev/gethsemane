package by.geth.gethsemane.ui.route.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.domain.model.Event
import by.geth.gethsemane.domain.repository.EventsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

class ScheduleViewModel(
    private val eventsRepository: EventsRepository,
): ViewModel() {
    private val _uiState: MutableStateFlow<ScheduleUiState> = MutableStateFlow(ScheduleUiState.None)
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    private val formatPattern = "dd.MM.yyyy HH:mm"

    @OptIn(FormatStringsInDatetimeFormats::class)
    private val dateTimeFormat = LocalDateTime.Format {
        byUnicodePattern(formatPattern)
    }

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

    fun format(dateTime: LocalDateTime): String {
        return dateTime.format(dateTimeFormat)
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
