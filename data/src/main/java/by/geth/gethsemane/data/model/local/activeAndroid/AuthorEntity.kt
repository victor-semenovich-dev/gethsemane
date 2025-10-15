package by.geth.gethsemane.data.model.local.activeAndroid

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table

@Table(name = "Authors", id = "_id")
data class AuthorEntity(
    @Column(name = COLUMN_ID) val id: Long = 0,
    @Column(name = COLUMN_NAME) val name: String = "",
    @Column(name = COLUMN_BIOGRAPHY) val biography: String = "",
): Model() {
    companion object {
        const val COLUMN_ID: String = "id"
        const val COLUMN_NAME: String = "name"
        const val COLUMN_BIOGRAPHY: String = "biography"
    }
}
