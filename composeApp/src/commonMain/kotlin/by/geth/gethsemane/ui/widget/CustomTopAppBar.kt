package by.geth.gethsemane.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CustomTopAppBar(
    navController: NavController,
    title: String,
    showBackButton: Boolean = true,
) {
    TopAppBar(
        modifier = Modifier
            .background(MaterialTheme.colors.primary)
            .statusBarsPadding(),
        navigationIcon = if (showBackButton) {
            @Composable {
                IconButton(
                    onClick = {
                        navController.navigateUp()
                    },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                    )
                }
            }
        } else null,
        elevation = 0.dp,
        title = {
            Text(
                text = title,
            )
        },
    )
}
