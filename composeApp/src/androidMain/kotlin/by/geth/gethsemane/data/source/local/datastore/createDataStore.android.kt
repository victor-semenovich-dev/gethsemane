package by.geth.gethsemane.data.source.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.Preferences

fun createDataStore(context: Context): DataStore<Preferences> {
    return createDataStore { context.dataStoreFile(DATA_STORE_FILE_NAME).absolutePath }
}
