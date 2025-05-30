package by.geth.gethsemane.ui.route.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import by.geth.gethsemane.domain.model.Event
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

class HomeViewModel: ViewModel() {
    var uiState by mutableStateOf(HomeState())
        private set

    @OptIn(FormatStringsInDatetimeFormats::class)
    val dateFormat = LocalDateTime.Format { byUnicodePattern("dd.MM.yyyy") }

    init {
        println("HomeViewModel init: $this")
    }

    fun updateCurrentEvent(event: Event) {
        println("onEventSelected: $this")
        uiState = uiState.copy(currentEvent = event)
    }
}

data class HomeState(
    val currentEvent: Event? = null,
)
