package by.geth.gethsemane.domain.repository

import by.geth.gethsemane.domain.model.Birthdays

interface BirthdaysRepository {
    suspend fun loadBirthdays(): Result<List<Birthdays>>
}
