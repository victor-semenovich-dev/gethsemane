package by.geth.gethsemane.ui.route.birthdays

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.domain.repository.BirthdaysRepository
import co.touchlab.kermit.Logger
import kotlinx.coroutines.launch

class BirthdaysViewModel(
    private val birthdaysRepository: BirthdaysRepository,
): ViewModel() {
    init {
        viewModelScope.launch {
            birthdaysRepository.loadBirthdays().onSuccess {
                Logger.d("MyTag") { "birthdays loaded" }
            }
        }
    }
}
