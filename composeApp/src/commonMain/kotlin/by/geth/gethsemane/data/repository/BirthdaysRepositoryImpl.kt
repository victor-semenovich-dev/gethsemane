package by.geth.gethsemane.data.repository

import by.geth.gethsemane.data.source.remote.service.BirthdaysService
import by.geth.gethsemane.domain.model.Birthdays
import by.geth.gethsemane.domain.repository.BirthdaysRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class BirthdaysRepositoryImpl(
    private val birthdaysService: BirthdaysService,
): BirthdaysRepository {
    override suspend fun loadBirthdays(): Result<List<Birthdays>> {
        return birthdaysService.getBirthdays().map { dtoList ->
            dtoList.map { dto ->
                val dateNow = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                var birthdaysDate = LocalDate(dateNow.year, dto.month, dto.day)
                if (birthdaysDate < dateNow) {
                    birthdaysDate = birthdaysDate.plus(1, DateTimeUnit.YEAR)
                }
                Birthdays(
                    date = birthdaysDate,
                    persons = dto.persons,
                )
            }
        }
    }
}
