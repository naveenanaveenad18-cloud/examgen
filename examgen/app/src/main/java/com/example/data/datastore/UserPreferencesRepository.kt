package com.example.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.api.ApiClient
import com.example.data.models.UserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val KEY_USER_ID = stringPreferencesKey("key_user_id")
        val KEY_USERNAME = stringPreferencesKey("key_username")
        val KEY_EMAIL = stringPreferencesKey("key_email")
        val KEY_ROLE = stringPreferencesKey("key_role")
        val KEY_FULL_NAME = stringPreferencesKey("key_full_name")
        val KEY_IS_LOGGED_IN = booleanPreferencesKey("key_is_logged_in")
        val KEY_SERVER_URL = stringPreferencesKey("key_server_url")
        val KEY_TOKEN = stringPreferencesKey("key_token")
    }

    val isLoggedInFlow: Flow<Boolean> = context.userDataStore.data.map { preferences ->
        preferences[KEY_IS_LOGGED_IN] ?: true // Default true for smooth instant exploration
    }

    val serverUrlFlow: Flow<String> = context.userDataStore.data.map { preferences ->
        preferences[KEY_SERVER_URL] ?: ApiClient.DEFAULT_BASE_URL
    }

    val currentUserFlow: Flow<UserDto> = context.userDataStore.data.map { preferences ->
        UserDto(
            id = preferences[KEY_USER_ID] ?: "usr_admin_1",
            username = preferences[KEY_USERNAME] ?: "admin",
            email = preferences[KEY_EMAIL] ?: "admin@examgen.edu",
            role = preferences[KEY_ROLE] ?: "Teacher",
            fullName = preferences[KEY_FULL_NAME] ?: "Prof. Alexander Vance"
        )
    }

    suspend fun saveUserSession(user: UserDto, token: String? = null) {
        context.userDataStore.edit { preferences ->
            preferences[KEY_USER_ID] = user.id
            preferences[KEY_USERNAME] = user.username
            preferences[KEY_EMAIL] = user.email
            preferences[KEY_ROLE] = user.role
            preferences[KEY_FULL_NAME] = user.fullName ?: user.username
            preferences[KEY_IS_LOGGED_IN] = true
            if (token != null) {
                preferences[KEY_TOKEN] = token
            }
        }
    }

    suspend fun saveServerUrl(url: String) {
        context.userDataStore.edit { preferences ->
            preferences[KEY_SERVER_URL] = url
        }
    }

    suspend fun logout() {
        context.userDataStore.edit { preferences ->
            preferences[KEY_IS_LOGGED_IN] = false
            preferences.remove(KEY_TOKEN)
        }
    }
}
