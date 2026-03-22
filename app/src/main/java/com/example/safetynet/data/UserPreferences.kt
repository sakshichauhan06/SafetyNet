package com.example.safetynet.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")


@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val EMAIL_KEY = stringPreferencesKey("remembered_email")
        val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me_checked")
    }

    val userEmail: Flow<String?> = context.dataStore.data.map { it[EMAIL_KEY] }
    val isRememberMe: Flow<Boolean> = context.dataStore.data.map { it[REMEMBER_ME_KEY] ?: false }

    suspend fun saveEmail(email: String, remember: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[EMAIL_KEY] = if (remember) email else ""
            prefs[REMEMBER_ME_KEY] = remember
        }
    }
}