package by.geth.gethsemane.domain.manager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import by.geth.gethsemane.domain.repository.MusicGroupsRepository
import by.geth.gethsemane.domain.util.PREFS_KEY_MUSIC_GROUPS_LOADED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class InitDataManager(
    private val prefsDataStore: DataStore<Preferences>,
    private val musicGroupsRepository: MusicGroupsRepository,
) {
    private val musicGroupsLoadedKey = booleanPreferencesKey(PREFS_KEY_MUSIC_GROUPS_LOADED)

    val dataLoadedFlow: Flow<Boolean> = prefsDataStore.data.map { preferences ->
        val musicGroupsLoaded = preferences[musicGroupsLoadedKey] ?: false
        musicGroupsLoaded
    }

    suspend fun loadInitialData(): Result<Unit> {
        val preferences = prefsDataStore.data.first()
        val musicGroupsLoaded = preferences[musicGroupsLoadedKey] ?: false
        if (!musicGroupsLoaded) {
            musicGroupsRepository.loadMusicGroups().onSuccess {
                prefsDataStore.edit { it[musicGroupsLoadedKey] = true }
            }.onFailure {
                return@loadInitialData Result.failure(it)
            }
        }
        return Result.success(Unit)
    }
}
