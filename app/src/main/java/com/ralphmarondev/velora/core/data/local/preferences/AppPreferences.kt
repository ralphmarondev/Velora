package com.ralphmarondev.velora.core.data.local.preferences

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences(
    context: Context
) {
    private val appContext = context.applicationContext
    private val dataStore = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler {
            emptyPreferences()
        },
        produceFile = {
            appContext.preferencesDataStoreFile(DATASTORE_NAME)
        }
    )

    companion object {
        private const val DATASTORE_NAME = "velora_preferences"
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val UID = stringPreferencesKey("uid")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val EMAIL = stringPreferencesKey("email")
        val PASSWORD = stringPreferencesKey("password") // TODO: hash this!
        val IMAGE_PATH = intPreferencesKey("image_path")
    }

    suspend fun setDarkMode(value: Boolean) {
        dataStore.edit { it[DARK_MODE] = value }
    }

    val isDarkMode: Flow<Boolean> = dataStore.data.map { it[DARK_MODE] ?: false }

    suspend fun setUid(value: String) {
        dataStore.edit { it[UID] = value }
    }

    val uid: Flow<String?> = dataStore.data.map { it[UID] }

    suspend fun setDisplayName(displayName: String) {
        dataStore.edit { it[DISPLAY_NAME] = displayName }
    }

    val displayName: Flow<String?> = dataStore.data.map { it[DISPLAY_NAME] }

    suspend fun setEmail(value: String) {
        dataStore.edit { it[EMAIL] = value }
    }

    val email: Flow<String?> = dataStore.data.map { it[EMAIL] }

    suspend fun setPassword(value: String) {
        dataStore.edit { it[PASSWORD] = value }
    }

    val password: Flow<String?> = dataStore.data.map { it[PASSWORD] }

    suspend fun setImagePath(imagePath: Int) {
        dataStore.edit { it[IMAGE_PATH] = imagePath }
    }

    val imagePath: Flow<Int> = dataStore.data.map { it[IMAGE_PATH] ?: 1 }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}