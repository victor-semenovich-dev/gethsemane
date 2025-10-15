package by.geth.gethsemane.ui.fragment.init

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.domain.repository.AuthorsRepository
import kotlinx.coroutines.launch

class InitViewModel(
    private val authorsRepository: AuthorsRepository,
): ViewModel() {
    sealed interface OneTimeEvent {
        data object DataLoaded: OneTimeEvent
        data object DataLoadingError: OneTimeEvent
    }

    private val _eventLiveData = MutableLiveData<OneTimeEvent?>()
    val eventLiveData: LiveData<OneTimeEvent?> get() = _eventLiveData

    fun loadData() {
        viewModelScope.launch {
            authorsRepository.loadAllAuthors().onSuccess {
                _eventLiveData.value = OneTimeEvent.DataLoaded
            }.onFailure {
                _eventLiveData.value = OneTimeEvent.DataLoadingError
            }
        }
    }

    fun consumeOneTimeEvent() {
        _eventLiveData.value = null
    }

    private companion object {
        const val TAG = "InitViewModel"
    }
}
