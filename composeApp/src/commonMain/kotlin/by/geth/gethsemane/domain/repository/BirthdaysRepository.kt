package by.geth.gethsemane.domain.repository

import by.geth.gethsemane.domain.model.Birthdays
import kotlinx.coroutines.flow.Flow

interface BirthdaysRepository {
    val birthdaysFlow: Flow<List<Birthdays>>

    suspend fun loadBirthdays(): Result<Unit>
}
