package by.geth.gethsemane.di.app

import by.geth.gethsemane.ui.fragment.init.InitViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelsModule = module {
    viewModelOf(::InitViewModel)
}
