package by.geth.gethsemane.data.source.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import by.geth.gethsemane.data.source.local.util.iosDocumentDirectory

fun createDataStore(): DataStore<Preferences> {
    return createDataStore {
        iosDocumentDirectory() + "/$DATA_STORE_FILE_NAME"
    }
}
