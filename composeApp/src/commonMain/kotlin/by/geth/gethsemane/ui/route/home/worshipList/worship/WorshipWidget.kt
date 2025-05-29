package by.geth.gethsemane.ui.route.home.worshipList.worship

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun WorshipWidget(
    modifier: Modifier = Modifier,
    eventId: Int,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = eventId.toString(),
            fontSize = 24.sp,
        )
    }
}
