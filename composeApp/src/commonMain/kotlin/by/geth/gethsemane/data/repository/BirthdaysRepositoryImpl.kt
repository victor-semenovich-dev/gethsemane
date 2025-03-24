package by.geth.gethsemane.data.repository

import by.geth.gethsemane.data.source.local.db.dao.BirthdaysDao
import by.geth.gethsemane.data.source.local.db.model.BirthdaysEntity
import by.geth.gethsemane.data.source.remote.model.BirthdaysDTO
import by.geth.gethsemane.data.source.remote.service.BirthdaysService
import by.geth.gethsemane.domain.repository.BirthdaysRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class BirthdaysRepositoryImpl(
    private val birthdaysService: BirthdaysService,
    private val birthdaysDao: BirthdaysDao,
): BirthdaysRepository {

    @OptIn(FormatStringsInDatetimeFormats::class)
    private val dateFormat = LocalDate.Format {
        byUnicodePattern("yyyy-MM-dd")
    }

    override suspend fun loadBirthdays(): Result<Unit> {
        return birthdaysService.getBirthdays().onSuccess { dtoList ->
            val dbEntitiesList = dtoList.map { dto -> dto.toDbModel() }
            birthdaysDao.clear()
            birthdaysDao.insertOrUpdate(*dbEntitiesList.toTypedArray())
        }.map {  }
    }

    private fun BirthdaysDTO.toDbModel(): BirthdaysEntity {
        val dateNow = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        var birthdaysDate = LocalDate(dateNow.year, this.month, this.day)
        if (birthdaysDate < dateNow) {
            birthdaysDate = birthdaysDate.plus(1, DateTimeUnit.YEAR)
        }
        return BirthdaysEntity(
            date = birthdaysDate.format(dateFormat),
            persons = this.persons.joinToString("|"),
        )
    }
}
