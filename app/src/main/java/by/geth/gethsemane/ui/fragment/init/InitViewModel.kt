package by.geth.gethsemane.ui.fragment.init

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.domain.repository.AuthorsRepository
import kotlinx.coroutines.launch

class InitViewModel(
    private val authorsRepository: AuthorsRepository,
): ViewModel() {
    private companion object {
        const val TAG = "InitViewModel"
    }

    init {
        viewModelScope.launch {
            Log.d(TAG, "get author: ${authorsRepository.getSingleAuthor(4)}")
            authorsRepository.loadSingleAuthor(4).onSuccess {
                Log.d(TAG, "loaded single author: $it")
            }
            Log.d(TAG, "get author: ${authorsRepository.getSingleAuthor(4)}")

            authorsRepository.loadAllAuthors().onSuccess {
                Log.d(TAG, "loaded ${it.size} authors")
            }
        }
    }
}
