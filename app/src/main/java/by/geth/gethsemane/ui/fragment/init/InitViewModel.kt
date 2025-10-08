package by.geth.gethsemane.ui.fragment.init

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.domain.repository.AuthorsRepository
import kotlinx.coroutines.launch

class InitViewModel(
    private val authorsRepository: AuthorsRepository,
): ViewModel() {

    fun loadAllAuthors() {
        viewModelScope.launch {
            authorsRepository.loadAllAuthors().onSuccess { authors ->
                // TODO authors loaded
                Log.d("MyTag", "${authors.size} authors loaded")
            }.onFailure {
                // TODO failed to load authors
                Log.e("MyTag", "failed to load authors")
            }
        }
    }
}
