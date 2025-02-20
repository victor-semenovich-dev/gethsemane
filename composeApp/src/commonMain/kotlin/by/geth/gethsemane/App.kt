package by.geth.gethsemane

import androidx.compose.runtime.Composable
import by.geth.gethsemane.ui.AppNavHost
import by.geth.gethsemane.ui.theme.AppTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        AppTheme {
            AppNavHost()
        }
    }
}
