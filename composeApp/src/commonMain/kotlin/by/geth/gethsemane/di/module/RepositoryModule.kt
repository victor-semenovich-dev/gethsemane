package by.geth.gethsemane.di.module

import by.geth.gethsemane.data.repository.AuthorsRepositoryImpl
import by.geth.gethsemane.data.repository.BirthdaysRepositoryImpl
import by.geth.gethsemane.data.repository.EventsRepositoryImpl
import by.geth.gethsemane.data.repository.MusicGroupsRepositoryImpl
import by.geth.gethsemane.domain.repository.AuthorsRepository
import by.geth.gethsemane.domain.repository.BirthdaysRepository
import by.geth.gethsemane.domain.repository.EventsRepository
import by.geth.gethsemane.domain.repository.MusicGroupsRepository
import org.koin.dsl.module

val repositoriesModule = module {
    single<EventsRepository> { EventsRepositoryImpl(get(), get()) }
    single<BirthdaysRepository> { BirthdaysRepositoryImpl(get(), get()) }
    single<MusicGroupsRepository> { MusicGroupsRepositoryImpl(get(), get(), get()) }
    single<AuthorsRepository> { AuthorsRepositoryImpl(get(), get(), get()) }
}