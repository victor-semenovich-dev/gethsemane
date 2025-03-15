package by.geth.gethsemane.ui.route.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import by.geth.gethsemane.domain.model.Schedule
import gethsemane.composeapp.generated.resources.Res
import gethsemane.composeapp.generated.resources.back
import gethsemane.composeapp.generated.resources.schedule
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleRoute(
    navController: NavController,
    viewModel: ScheduleViewModel = koinViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back),
                        )
                    }
                },
                title = { Text(text = stringResource(Res.string.schedule)) },
            )
        },
    ) { contentPadding ->
        ScheduleList(
            modifier = Modifier.padding(
                top = contentPadding.calculateTopPadding(),
                start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = contentPadding.calculateEndPadding(LayoutDirection.Ltr),
            ),
            schedule = viewModel.uiState.schedule,
        )
    }
}

@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
fun ScheduleList(
    modifier: Modifier = Modifier,
    schedule: Schedule,
) {
    val dateTimeFormat = LocalDateTime.Format {
        byUnicodePattern("dd.MM.yyyy HH:mm")
    }
    LazyColumn(modifier = modifier) {
        items(schedule.items) { scheduleItem ->
            val dateTime = scheduleItem.dateTime.format(dateTimeFormat)
            val musicGroup = scheduleItem.musicGroup
            val subtitle = if (musicGroup != null) "$dateTime • $musicGroup" else dateTime
            ScheduleListItem(
                title = scheduleItem.title,
                subTitle = subtitle,
            )
        }
    }
}

@Composable
fun ScheduleListItem(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = title)
            Text(text = subTitle)
        }
        HorizontalDivider()
    }
}
