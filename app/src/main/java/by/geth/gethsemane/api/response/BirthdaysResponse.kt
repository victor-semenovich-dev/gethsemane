package by.geth.gethsemane.api.response

import by.geth.gethsemane.data.model.Birthday
import com.google.gson.annotations.SerializedName

class BirthdaysResponse(
    @SerializedName("month")
    var month: Int,

    @SerializedName("day")
    var day: Int,

    @SerializedName("persons")
    var persons: List<String>
) {
    val dbEntity: Birthday
        get() = Birthday().apply {
            month = this@BirthdaysResponse.month
            day = this@BirthdaysResponse.day
            persons = this@BirthdaysResponse.persons
        }
}