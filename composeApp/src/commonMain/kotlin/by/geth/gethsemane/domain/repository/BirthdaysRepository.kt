package by.geth.gethsemane.domain.repository

interface BirthdaysRepository {
    suspend fun loadBirthdays(): Result<Unit>
}
