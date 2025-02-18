package by.geth.gethsemane.ui.route.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.domain.manager.ScheduleManager
import by.geth.gethsemane.domain.model.Schedule
import by.geth.gethsemane.domain.model.ScheduleItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

class ScheduleViewModel(
    private val scheduleManager: ScheduleManager,
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
            scheduleManager.loadSchedule().onSuccess { schedule ->
                _uiState.value = ScheduleUiState.Success(schedule)
            }.onFailure { reason ->
                _uiState.value = ScheduleUiState.Failure(reason)
            }
        }
    }

    fun buildSubtitle(item: ScheduleItem): String {
        val dateTime = item.dateTime.format(dateTimeFormat)
        val musicGroup = item.musicGroup
        return if (musicGroup != null) "$dateTime • $musicGroup" else dateTime
    }
}

sealed class ScheduleUiState {
    data object None: ScheduleUiState()
    data object Loading: ScheduleUiState()
    data class Success(
        val schedule: Schedule,
    ): ScheduleUiState()
    data class Failure(
        val reason: Throwable,
    ): ScheduleUiState()
}
