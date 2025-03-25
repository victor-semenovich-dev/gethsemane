package by.geth.gethsemane.ui.route.birthdays

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.domain.model.Birthdays
import by.geth.gethsemane.domain.repository.BirthdaysRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BirthdaysViewModel(
    private val birthdaysRepository: BirthdaysRepository,
): ViewModel() {
    var uiState: BirthdaysUiState by mutableStateOf(BirthdaysUiState())
        private set

    init {
        observeData()
        loadData()
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
            uiState = uiState.copy(isLoading = true, error = null)
            birthdaysRepository.loadBirthdays().onSuccess {
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

data class BirthdaysUiState(
    val birthdays: List<Birthdays> = emptyList(),
    val isLoading: Boolean = false,
    val error: Throwable? = null,
)
