package by.geth.gethsemane.ui.route.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import by.geth.gethsemane.ui.BirthdaysRoute
import by.geth.gethsemane.ui.LocalNavController
import by.geth.gethsemane.ui.LocalSnackbarHostState
import by.geth.gethsemane.ui.ScheduleRoute
import by.geth.gethsemane.ui.route.home.worshipList.WorshipListWidget
import by.geth.gethsemane.ui.theme.StatusBarAppearance
import by.geth.gethsemane.ui.util.formattedTitle
import by.geth.gethsemane.ui.widget.CustomTopAppBar
import by.geth.gethsemane.ui.widget.TopAppBarIconButton
import gethsemane.composeapp.generated.resources.Res
import gethsemane.composeapp.generated.resources.birthdays
import gethsemane.composeapp.generated.resources.gethsemane
import gethsemane.composeapp.generated.resources.menu
import gethsemane.composeapp.generated.resources.schedule
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = koinViewModel(),
) {
    if (!viewModel.uiState.initialDataLoaded) {
        HomeRouteProgress()
    } else {
        HomeRouteContent(viewModel)
    }
}

@Composable
private fun HomeRouteProgress() {
    Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator(Modifier.align(Alignment.Center))
    }
}

@Composable
private fun HomeRouteContent(viewModel: HomeViewModel) {
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
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold(
            topBar = {
                CustomTopAppBar(
                    title = viewModel.uiState.currentEvent?.formattedTitle ?:
                        stringResource(Res.string.gethsemane),
                    navigationIcon = {
                        TopAppBarIconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            imageVector = Icons.Default.Menu,
                            contentDescription = stringResource(Res.string.menu),
                        )
                    }
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { contentPadding ->
            CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
                WorshipListWidget(
                    modifier = Modifier.padding(contentPadding).padding(horizontal = 8.dp),
                )
            }
        }
    }
}