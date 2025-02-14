package by.geth.gethsemane.di

import by.geth.gethsemane.data.repository.EventsRepositoryImpl
import by.geth.gethsemane.domain.repository.EventsRepository
import by.geth.gethsemane.ui.route.schedule.ScheduleViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val repositoriesModule = module {
    single<EventsRepository> { EventsRepositoryImpl() }
}

val viewModelsModule = module {
    viewModelOf(::ScheduleViewModel)
}
