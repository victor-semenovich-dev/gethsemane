package by.geth.gethsemane.di

import by.geth.gethsemane.data.repository.AuthorsRepositoryImpl
import by.geth.gethsemane.domain.repository.AuthorsRepository
import org.koin.dsl.module

val repositoriesModule = module {
    single<AuthorsRepository> { AuthorsRepositoryImpl() }
}