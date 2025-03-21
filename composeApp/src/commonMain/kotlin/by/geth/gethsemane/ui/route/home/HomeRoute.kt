package by.geth.gethsemane.ui.route.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import by.geth.gethsemane.ui.ScheduleRoute
import by.geth.gethsemane.ui.widget.CustomTopAppBar
import gethsemane.composeapp.generated.resources.Res
import gethsemane.composeapp.generated.resources.gethsemane
import gethsemane.composeapp.generated.resources.menu
import gethsemane.composeapp.generated.resources.schedule
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeRoute(
    navController: NavController,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { Text(stringResource(Res.string.schedule)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(ScheduleRoute)
                    },
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CustomTopAppBar(
                    title = stringResource(Res.string.gethsemane),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(Res.string.menu),
                            )
                        }
                    }
                )
            },
        ) { contentPadding ->
            Column(
                modifier = Modifier.padding(contentPadding).padding(horizontal = 8.dp),
            ) {
            }
        }
    }
}
