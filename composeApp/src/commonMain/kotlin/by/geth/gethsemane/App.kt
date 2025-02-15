package by.geth.gethsemane

import androidx.compose.runtime.Composable
import by.geth.gethsemane.di.httpModule
import by.geth.gethsemane.di.repositoriesModule
import by.geth.gethsemane.di.servicesModule
import by.geth.gethsemane.di.viewModelsModule
import by.geth.gethsemane.ui.AppNavHost
import by.geth.gethsemane.ui.theme.AppTheme
import org.koin.compose.KoinApplication

@Composable
fun App() {
    KoinApplication(
        application = {
            modules(httpModule, servicesModule, repositoriesModule, viewModelsModule)
        },
    ) {
        AppTheme {
            AppNavHost()
        }
    }
}
