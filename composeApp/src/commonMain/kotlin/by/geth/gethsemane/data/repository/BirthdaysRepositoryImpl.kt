package by.geth.gethsemane.data.repository

import by.geth.gethsemane.data.source.local.db.dao.BirthdaysDao
import by.geth.gethsemane.data.source.local.db.model.BirthdaysEntity
import by.geth.gethsemane.data.source.remote.model.BirthdaysDTO
import by.geth.gethsemane.data.source.remote.service.BirthdaysService
import by.geth.gethsemane.domain.model.Birthdays
import by.geth.gethsemane.domain.repository.BirthdaysRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class BirthdaysRepositoryImpl(
    private val birthdaysService: BirthdaysService,
    private val birthdaysDao: BirthdaysDao,
): BirthdaysRepository {

    override val birthdaysFlow: Flow<List<Birthdays>> = birthdaysDao.getAll().map { entityList ->
        val dateNow = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        entityList.map { it.toDomainModel() }.filter {
            it.date >= dateNow
        }.sortedBy { it.date }
    }

    override suspend fun loadBirthdays(): Result<List<Birthdays>> {
        return birthdaysService.getBirthdays().map { dtoList ->
            val dbEntitiesList = dtoList.map { dto -> dto.toDbModel() }
            birthdaysDao.replaceAll(dbEntitiesList)
            dbEntitiesList.map { it.toDomainModel() }
        }
    }

    private fun BirthdaysDTO.toDbModel(): BirthdaysEntity {
        val dateNow = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        var birthdaysDate = LocalDate(dateNow.year, this.month, this.day)
        if (birthdaysDate < dateNow) {
            birthdaysDate = birthdaysDate.plus(1, DateTimeUnit.YEAR)
        }
        return BirthdaysEntity(
            date = birthdaysDate,
            persons = this.persons.joinToString("|"),
        )
    }

    private fun BirthdaysEntity.toDomainModel() = Birthdays(
        date = this.date,
        persons = this.persons.split("|"),
    )
}
