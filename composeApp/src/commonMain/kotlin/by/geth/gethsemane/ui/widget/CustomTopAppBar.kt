package by.geth.gethsemane.ui.widget

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import by.geth.gethsemane.ui.LocalNavController
import gethsemane.composeapp.generated.resources.Res
import gethsemane.composeapp.generated.resources.back
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String,
    navigationIcon: @Composable () -> Unit = {},
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = navigationIcon,
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        },
    )
}

@Composable
fun BackNavigationTopAppBar(
    title: String,
) {
    val navController = LocalNavController.current
    CustomTopAppBar(
        title = title,
        navigationIcon = {
            TopAppBarIconButton(
                onClick = { navController.navigateUp() },
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.back),
            )
        }
    )
}

@Composable
fun TopAppBarIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String? = null,
) {
    IconButton(modifier = modifier, onClick = onClick) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

