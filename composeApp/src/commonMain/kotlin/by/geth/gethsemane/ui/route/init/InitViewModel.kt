package by.geth.gethsemane.ui.route.init

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.domain.manager.InitDataManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class InitViewModel(
    private val initDataManager: InitDataManager,
): ViewModel() {
    var uiState: InitUiState by mutableStateOf(InitUiState())
        private set

    init {
        observeDataLoaded()
        loadData()
    }

    private fun observeDataLoaded() {
        viewModelScope.launch {
            initDataManager.dataLoadedFlow.collectLatest { isLoaded ->
                uiState = uiState.copy(isDataLoaded = isLoaded)
            }
        }
    }

    fun loadData() {
        viewModelScope.launch {
            uiState = uiState.copy(isDataLoading = true, isFailedToLoadData = false)
            initDataManager.loadInitialData().onSuccess {
                uiState = uiState.copy(isDataLoading = false, isDataLoaded = true)
            }.onFailure {
                uiState = uiState.copy(isDataLoading = false, isFailedToLoadData = true)
            }
        }
    }
}

data class InitUiState(
    val isDataLoaded: Boolean = false,
    val isDataLoading: Boolean = false,
    val isFailedToLoadData: Boolean = false,
)
