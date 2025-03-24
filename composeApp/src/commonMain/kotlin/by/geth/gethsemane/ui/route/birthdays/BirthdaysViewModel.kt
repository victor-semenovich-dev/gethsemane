package by.geth.gethsemane.ui.route.birthdays

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.data.source.remote.service.BirthdaysService
import co.touchlab.kermit.Logger
import kotlinx.coroutines.launch

class BirthdaysViewModel(
    private val birthdaysService: BirthdaysService,
): ViewModel() {
    init {
        viewModelScope.launch {
            birthdaysService.getBirthdays().onSuccess {
                Logger.d("MyTag") { "birthdays: $it" }
            }
        }
    }
}
