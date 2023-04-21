package org.prikic.flightsearch.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.prikic.flightsearch.TAG
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {

    val searchText: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[SEARCH_TEXT] ?: ""
        }

    private companion object {
        val SEARCH_TEXT = stringPreferencesKey("search_text")
    }

    suspend fun saveSearchTextPreference(searchText: String) {
        dataStore.edit {preferences ->
            preferences[SEARCH_TEXT] = searchText

        }
    }

}
