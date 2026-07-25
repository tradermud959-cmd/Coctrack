package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val KEY_USERNAME = stringPreferencesKey("username")
        val KEY_TARGET_GEMS = intPreferencesKey("target_gems")
        val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")
        val KEY_SHOWN_ACHIEVEMENTS = stringSetPreferencesKey("shown_achievements")
    }

    val usernameFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[KEY_USERNAME] ?: ""
    }

    val targetGemsFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[KEY_TARGET_GEMS] ?: 500
    }

    val darkThemeFlow: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[KEY_DARK_THEME]
    }

    val shownAchievementsFlow: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[KEY_SHOWN_ACHIEVEMENTS] ?: emptySet()
    }

    suspend fun saveUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[KEY_USERNAME] = username
        }
    }

    suspend fun saveTargetGems(target: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_TARGET_GEMS] = target
        }
    }

    suspend fun saveDarkTheme(isDark: Boolean?) {
        dataStore.edit { preferences ->
            if (isDark == null) {
                preferences.remove(KEY_DARK_THEME)
            } else {
                preferences[KEY_DARK_THEME] = isDark
            }
        }
    }

    suspend fun addShownAchievement(id: String) {
        dataStore.edit { preferences ->
            val current = preferences[KEY_SHOWN_ACHIEVEMENTS] ?: emptySet()
            preferences[KEY_SHOWN_ACHIEVEMENTS] = current + id
        }
    }

    suspend fun resetAchievements() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_SHOWN_ACHIEVEMENTS)
        }
    }

    suspend fun clearPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
