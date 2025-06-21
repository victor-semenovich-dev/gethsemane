package by.geth.gethsemane.di.module

import by.geth.gethsemane.ui.route.birthdays.BirthdaysViewModel
import by.geth.gethsemane.ui.route.home.HomeViewModel
import by.geth.gethsemane.ui.route.home.worshipList.WorshipListViewModel
import by.geth.gethsemane.ui.route.schedule.ScheduleViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelsModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::WorshipListViewModel)

    viewModelOf(::ScheduleViewModel)
    viewModelOf(::BirthdaysViewModel)
}
