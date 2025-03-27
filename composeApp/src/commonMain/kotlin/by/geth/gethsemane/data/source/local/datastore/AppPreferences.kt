package by.geth.gethsemane.data.source.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences(
    private val dataStore: DataStore<Preferences>,
) {
    private val musicGroupsLoadedKey = booleanPreferencesKey("musicGroupsLoaded")

    val musicGroupsLoaded: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[musicGroupsLoadedKey] ?: false
    }

    suspend fun setMusicGroupsLoaded() {
        dataStore.edit { preferences -> preferences[musicGroupsLoadedKey] = true }
    }
}
