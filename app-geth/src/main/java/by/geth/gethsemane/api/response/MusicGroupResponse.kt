package by.geth.gethsemane.api.response

import by.geth.gethsemane.data.MusicGroup
import com.google.gson.annotations.SerializedName

class MusicGroupResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("history")
    val history: String,

    @SerializedName("leader")
    val leader: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("isActive")
    val isActive: Boolean
) {
    val dbEntity: MusicGroup
        get() = MusicGroup().apply {
            this.externalId = this@MusicGroupResponse.id
            this.title = this@MusicGroupResponse.title
            this.history = this@MusicGroupResponse.history
            this.leader = this@MusicGroupResponse.leader
            this.image = this@MusicGroupResponse.image
            this.isShow = this@MusicGroupResponse.isActive
        }
}