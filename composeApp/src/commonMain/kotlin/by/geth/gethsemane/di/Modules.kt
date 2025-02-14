package by.geth.gethsemane.di

import by.geth.gethsemane.ui.route.schedule.ScheduleViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModules = module {
    viewModelOf(::ScheduleViewModel)
}
