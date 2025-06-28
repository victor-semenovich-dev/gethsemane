package by.geth.gethsemane.ui.route.home.worshipList

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.domain.model.Event
import by.geth.gethsemane.domain.repository.EventsRepository
import by.geth.gethsemane.domain.util.dateNow
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

class WorshipListViewModel(
    private val eventsRepository: EventsRepository,
): ViewModel() {
    companion object {
        private const val LOG_TAG = "WorshipListViewModel"
        val loadMoreEventsDatePeriod = DatePeriod(months = 1)
    }

    private val eventsChannel = Channel<WorshipListEvent>()
    val eventsFlow = eventsChannel.receiveAsFlow()

    var uiState: WorshipListState by mutableStateOf(WorshipListState())
        private set

    private var loadDataJob: Job? = null
    private var dateFrom: LocalDate = dateNow - loadMoreEventsDatePeriod
    private var eventsLoaded: Int = 0
    private var canLoadMoreEvents: Boolean = true

    private val worshipEventsFlow = eventsRepository.eventsFlow.map { allEvents ->
        withContext(Dispatchers.Default) {
            allEvents.filter { event ->
                event.categoryId == Event.WORSHIP_CATEGORY_ID && !event.isDraft
            }.sortedByDescending { it.dateTime }
        }
    }

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            worshipEventsFlow.collectLatest { worshipEvents ->
                Logger.d(LOG_TAG) { "${worshipEvents.size} events" }
                uiState = uiState.copy(worshipEvents = worshipEvents)
            }
        }
    }

    fun loadData() {
        loadDataJob = viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            Logger.d(LOG_TAG) { "load events from $dateFrom" }
            eventsRepository.loadEvents(dateFrom = dateFrom, replaceAll = true).onSuccess {
                canLoadMoreEvents = it.size > eventsLoaded
                eventsLoaded = it.size
                Logger.d(LOG_TAG) { "loaded $eventsLoaded events" }
                uiState = uiState.copy(isLoading = false)
            }.onFailure { error ->
                uiState = uiState.copy(isLoading = false)
                eventsChannel.send(WorshipListEvent.ErrorEvent(error))
            }
        }
    }

    fun onCurrentPageChanged(page: Int) {
        Logger.d(LOG_TAG) { "onCurrentPageChanged: $page" }
        viewModelScope.launch {
            loadDataJob?.join()
            if (page >= uiState.worshipEvents.size - 3 && canLoadMoreEvents) {
                Logger.d(LOG_TAG) { "load more events" }
                dateFrom -= loadMoreEventsDatePeriod
                loadData()
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
