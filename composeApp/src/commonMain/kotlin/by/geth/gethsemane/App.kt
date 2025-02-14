package by.geth.gethsemane

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import by.geth.gethsemane.di.repositoriesModule
import by.geth.gethsemane.di.viewModelsModule
import by.geth.gethsemane.ui.AppNavHost
import org.koin.compose.KoinApplication

@Composable
fun App() {
    KoinApplication(
        application = {
            modules(repositoriesModule, viewModelsModule)
        },
    ) {
        MaterialTheme {
            AppNavHost()
        }
    }
}
