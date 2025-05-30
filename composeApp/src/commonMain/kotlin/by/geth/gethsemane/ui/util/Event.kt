package by.geth.gethsemane.ui.util

import androidx.compose.runtime.Composable
import by.geth.gethsemane.domain.model.Event
import gethsemane.composeapp.generated.resources.Res
import gethsemane.composeapp.generated.resources.day_of_week_short
import gethsemane.composeapp.generated.resources.evening
import gethsemane.composeapp.generated.resources.months_genitive
import gethsemane.composeapp.generated.resources.morning
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

val Event.formattedTitle: String
    @Composable
    get() {
        val monthNames = stringArrayResource(Res.array.months_genitive)
        val dayOfWeekNames = stringArrayResource(Res.array.day_of_week_short)
        val morning = stringResource(Res.string.morning)
        val evening = stringResource(Res.string.evening)
        val format = LocalDateTime.Format {
            dayOfMonth()
            char(' ')
            monthName(MonthNames(monthNames))
            char(' ')
            year()

            chars(" (")
            dayOfWeek(DayOfWeekNames(dayOfWeekNames))
            if (dateTime.dayOfWeek == DayOfWeek.SUNDAY) {
                chars(", ")
                if (dateTime.hour <= 12) {
                    chars(morning)
                } else {
                    chars(evening)
                }
            }
            chars(")")
        }
        return format.format(dateTime)
    }
