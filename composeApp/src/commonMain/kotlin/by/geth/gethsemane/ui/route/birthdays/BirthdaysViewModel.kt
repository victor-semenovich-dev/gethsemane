package by.geth.gethsemane.ui.route.birthdays

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.domain.model.Birthdays
import by.geth.gethsemane.domain.repository.BirthdaysRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class BirthdaysViewModel(
    private val birthdaysRepository: BirthdaysRepository,
): ViewModel() {
    private val eventsChannel = Channel<BirthdaysEvent>()
    val eventsFlow = eventsChannel.receiveAsFlow()

    var uiState: BirthdaysUiState by mutableStateOf(BirthdaysUiState())
        private set

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            birthdaysRepository.birthdaysFlow.collectLatest { birthdays ->
                uiState = uiState.copy(birthdays = birthdays)
            }
        }
    }

    fun loadData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            birthdaysRepository.loadBirthdays().onSuccess {
                uiState = uiState.copy(isLoading = false)
            }.onFailure { error ->
                uiState = uiState.copy(isLoading = false)
                eventsChannel.send(BirthdaysEvent.ErrorEvent(error))
            }
        }
    }
}

data class BirthdaysUiState(
    val birthdays: List<Birthdays> = emptyList(),
    val isLoading: Boolean = false,
)

sealed interface BirthdaysEvent {
    data class ErrorEvent(val error: Throwable): BirthdaysEvent
}
