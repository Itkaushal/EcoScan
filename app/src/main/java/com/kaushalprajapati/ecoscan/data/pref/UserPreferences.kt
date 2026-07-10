package com.kaushalprajapati.ecoscan.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_NAME = stringPreferencesKey("user_name")
        val PROFILE_PICTURE_URI = stringPreferencesKey("profile_picture_uri")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[IS_LOGGED_IN] ?: false }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[USER_EMAIL] }
    val userName: Flow<String?> = context.dataStore.data.map { it[USER_NAME] }
    val profilePictureUri: Flow<String?> = context.dataStore.data.map { it[PROFILE_PICTURE_URI] }
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { it[NOTIFICATIONS_ENABLED] ?: true }
    val darkModeEnabled: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE_ENABLED] ?: false }

    suspend fun saveUser(name: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[USER_NAME] = name
            preferences[USER_EMAIL] = email
        }
    }

    suspend fun updateProfile(name: String, email: String, photoUri: String?) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
            preferences[USER_EMAIL] = email
            if (photoUri != null) preferences[PROFILE_PICTURE_URI] = photoUri
        }
    }

    suspend fun toggleNotifications(enabled: Boolean) {
        context.dataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun toggleDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE_ENABLED] = enabled }
    }

    suspend fun clearUser() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
            preferences[USER_NAME] = ""
            preferences[USER_EMAIL] = ""
        }
    }
}
