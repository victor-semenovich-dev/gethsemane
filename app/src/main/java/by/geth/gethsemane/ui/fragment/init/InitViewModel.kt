package by.geth.gethsemane.ui.fragment.init

import android.util.Log
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
            Log.d(LOG_TAG, "load all authors")
            authorsRepository.loadAllAuthors().onSuccess {
                Log.d(LOG_TAG, "load all authors: success")
                _eventLiveData.value = OneTimeEvent.DataLoaded
            }.onFailure {
                Log.d(LOG_TAG, "load all authors: error")
                _eventLiveData.value = OneTimeEvent.DataLoadingError
            }
        }
    }

    fun consumeOneTimeEvent() {
        _eventLiveData.value = null
    }

    private companion object {
        const val LOG_TAG = "InitViewModel"
    }
}
