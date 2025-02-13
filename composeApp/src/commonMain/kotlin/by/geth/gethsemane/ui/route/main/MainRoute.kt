package by.geth.gethsemane.ui.route.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MainRoute() {
    Box(Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Main route",
        )
    }
}
