package by.geth.gethsemane.ui.route.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeRoute() {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .background(MaterialTheme.colors.primary)
                    .statusBarsPadding(),
                elevation = 0.dp,
                title = {
                    Text(
                        text = "Гефсимания",
                    )
                },
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp),
        ) {
            Button(
                onClick = {},
            ) {
                Text("Расписание")
            }
        }
    }
}
