package by.geth.gethsemane.ui.fragment.init

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.domain.repository.AuthorsRepository
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.launch

class InitViewModel(
    private val authorsRepository: AuthorsRepository,
): ViewModel() {

    fun loadAllAuthors() {
        viewModelScope.launch {
            Log.d("MyTag", "author: ${authorsRepository.getSingleAuthor(4).singleOrNull()}")
            Log.d("MyTag", "load data")
            authorsRepository.loadAllAuthors().onSuccess { authors ->
                Log.d("MyTag", "${authors.size} authors loaded")
                Log.d("MyTag", "author: ${authorsRepository.getSingleAuthor(4).singleOrNull()}")
            }.onFailure {
                Log.e("MyTag", "failed to load authors", it)
            }
        }
    }
}
