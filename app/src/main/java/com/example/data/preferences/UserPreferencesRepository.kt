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
        val KEY_APP_THEME = stringPreferencesKey("app_theme")
        val KEY_SHOWN_ACHIEVEMENTS = stringSetPreferencesKey("shown_achievements")
        
        // AI Preferences
        val KEY_AI_MODE = stringPreferencesKey("ai_mode")
        val KEY_AI_PROVIDER = stringPreferencesKey("ai_provider")
        val KEY_GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        val KEY_GROQ_API_KEY = stringPreferencesKey("groq_api_key")

        // Profiles
        val KEY_ACTIVE_PROFILE_ID = intPreferencesKey("active_profile_id")
        
        fun usernameKeyFor(profileId: Int) = if (profileId == 1) KEY_USERNAME else stringPreferencesKey("username_$profileId")
        fun targetGemsKeyFor(profileId: Int) = if (profileId == 1) KEY_TARGET_GEMS else intPreferencesKey("target_gems_$profileId")
    }

    val activeProfileIdFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[KEY_ACTIVE_PROFILE_ID] ?: 1
    }

    val usernameFlow: Flow<String> = dataStore.data.map { preferences ->
        val profileId = preferences[KEY_ACTIVE_PROFILE_ID] ?: 1
        preferences[usernameKeyFor(profileId)] ?: ""
    }

    val targetGemsFlow: Flow<Int> = dataStore.data.map { preferences ->
        val profileId = preferences[KEY_ACTIVE_PROFILE_ID] ?: 1
        preferences[targetGemsKeyFor(profileId)] ?: 500
    }

    fun getUsernameForProfile(profileId: Int): Flow<String> = dataStore.data.map { preferences ->
        preferences[usernameKeyFor(profileId)] ?: ""
    }

    val darkThemeFlow: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[KEY_DARK_THEME]
    }

    val appThemeFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[KEY_APP_THEME] ?: "default"
    }

    val shownAchievementsFlow: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[KEY_SHOWN_ACHIEVEMENTS] ?: emptySet()
    }

    val aiModeFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[KEY_AI_MODE] ?: "offline"
    }

    val aiProviderFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[KEY_AI_PROVIDER] ?: "gemini"
    }

    val geminiApiKeyFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[KEY_GEMINI_API_KEY] ?: ""
    }

    val groqApiKeyFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[KEY_GROQ_API_KEY] ?: ""
    }

    suspend fun saveAiMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[KEY_AI_MODE] = mode
        }
    }

    suspend fun saveAiProvider(provider: String) {
        dataStore.edit { preferences ->
            preferences[KEY_AI_PROVIDER] = provider
        }
    }

    suspend fun saveGeminiApiKey(key: String) {
        dataStore.edit { preferences ->
            preferences[KEY_GEMINI_API_KEY] = key
        }
    }

    suspend fun saveGroqApiKey(key: String) {
        dataStore.edit { preferences ->
            preferences[KEY_GROQ_API_KEY] = key
        }
    }

    suspend fun saveActiveProfileId(id: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_ACTIVE_PROFILE_ID] = id
        }
    }

    suspend fun saveUsername(profileId: Int, username: String) {
        dataStore.edit { preferences ->
            preferences[usernameKeyFor(profileId)] = username
        }
    }

    suspend fun saveTargetGems(profileId: Int, target: Int) {
        dataStore.edit { preferences ->
            preferences[targetGemsKeyFor(profileId)] = target
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

    suspend fun saveAppTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[KEY_APP_THEME] = theme
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
