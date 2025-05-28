package by.geth.gethsemane.ui.route.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
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
import by.geth.gethsemane.ui.BirthdaysRoute
import by.geth.gethsemane.ui.LocalNavController
import by.geth.gethsemane.ui.ScheduleRoute
import by.geth.gethsemane.ui.theme.StatusBarAppearance
import by.geth.gethsemane.ui.widget.CustomTopAppBar
import by.geth.gethsemane.ui.widget.TopAppBarIconButton
import gethsemane.composeapp.generated.resources.Res
import gethsemane.composeapp.generated.resources.birthdays
import gethsemane.composeapp.generated.resources.gethsemane
import gethsemane.composeapp.generated.resources.menu
import gethsemane.composeapp.generated.resources.schedule
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeRoute() {
    val navController = LocalNavController.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    StatusBarAppearance(lightIcons = drawerState.targetValue == DrawerValue.Closed)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { Text(stringResource(Res.string.schedule)) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.snapTo(DrawerValue.Closed)
                            navController.navigate(ScheduleRoute)
                        }
                    },
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(Res.string.birthdays)) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.snapTo(DrawerValue.Closed)
                            navController.navigate(BirthdaysRoute)
                        }
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
                        TopAppBarIconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            imageVector = Icons.Default.Menu,
                            contentDescription = stringResource(Res.string.menu),
                        )
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
