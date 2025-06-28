package by.geth.gethsemane.ui.route.home.worshipList.worship

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.domain.repository.WorshipRepository
import kotlinx.coroutines.launch

class WorshipViewModel(
    private val eventId: Int,
    private val worshipRepository: WorshipRepository,
): ViewModel() {
    var uiState by mutableStateOf(WorshipState())
        private set

    fun loadWorship() {
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = true,
                isLoaded = false,
                isError = false,
            )
            worshipRepository.loadWorship(eventId).onSuccess {
                uiState = uiState.copy(
                    isLoading = false,
                    isLoaded = true,
                    isError = false,
                )
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    isLoaded = false,
                    isError = true,
                )
            }
        }
    }
}

data class WorshipState(
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val isError: Boolean = false,
)
