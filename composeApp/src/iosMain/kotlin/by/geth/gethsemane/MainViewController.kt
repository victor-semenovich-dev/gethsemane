package by.geth.gethsemane

import androidx.compose.ui.window.ComposeUIViewController
import by.geth.gethsemane.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }