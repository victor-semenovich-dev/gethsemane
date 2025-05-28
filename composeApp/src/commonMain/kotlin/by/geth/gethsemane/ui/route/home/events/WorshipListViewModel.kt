package by.geth.gethsemane.ui.route.home.events

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.domain.model.Event
import by.geth.gethsemane.domain.repository.EventsRepository
import by.geth.gethsemane.domain.util.dateNow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.minus

class WorshipListViewModel(
    private val eventsRepository: EventsRepository,
): ViewModel() {
    companion object {
        private const val WORSHIP_CATEGORY_ID = 10
    }

    private val eventsChannel = Channel<WorshipListEvent>()
    val eventsFlow = eventsChannel.receiveAsFlow()

    var uiState: WorshipListState by mutableStateOf(WorshipListState())
        private set

    private val worshipEventsFlow = eventsRepository.eventsFlow.map { allEvents ->
        withContext(Dispatchers.Default) {
            allEvents.filter { event ->
                event.categoryId == WORSHIP_CATEGORY_ID && !event.isDraft
            }.sortedByDescending { it.dateTime }
        }
    }

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            worshipEventsFlow.collectLatest { worshipEvents ->
                uiState = uiState.copy(worshipEvents = worshipEvents)
            }
        }
    }

    fun loadData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            // TODO load more events on pager scrolled
            eventsRepository.loadEvents(dateFrom = dateNow - DatePeriod(months = 1)).onSuccess {
                uiState = uiState.copy(isLoading = false)
            }.onFailure { error ->
                uiState = uiState.copy(isLoading = false)
            }
        }
    }
}

data class WorshipListState (
    val worshipEvents: List<Event> = emptyList(),
    val isLoading: Boolean = false,
)

sealed interface WorshipListEvent {
    data class ErrorEvent(val error: Throwable): WorshipListEvent
}
