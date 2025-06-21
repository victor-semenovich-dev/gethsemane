package by.geth.gethsemane.di.module

import by.geth.gethsemane.domain.usecase.LoadInitialDataUseCase
import by.geth.gethsemane.domain.usecase.LoadScheduleUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCaseModule = module {
    singleOf(::LoadScheduleUseCase)
    singleOf(::LoadInitialDataUseCase)
}