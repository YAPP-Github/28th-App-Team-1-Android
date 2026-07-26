package com.dminus14.app.data.local.installation

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val INSTALLATION_ID_DATA_STORE_NAME = "installation_id"
private const val INSTALLATION_ID_KEY_NAME = "installation_id"

private val Context.installationIdDataStore: DataStore<Preferences> by preferencesDataStore(
    name = INSTALLATION_ID_DATA_STORE_NAME,
)

interface InstallationIdStore {
    suspend fun get(): String?

    suspend fun set(value: String)
}

@Singleton
class PreferencesInstallationIdStore
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : InstallationIdStore {
        private val dataStore = context.installationIdDataStore

        override suspend fun get(): String? =
            dataStore.data
                .map { preferences -> preferences[INSTALLATION_ID_KEY] }
                .first()

        override suspend fun set(value: String) {
            dataStore.edit { preferences ->
                preferences[INSTALLATION_ID_KEY] = value
            }
        }

        private companion object {
            val INSTALLATION_ID_KEY = stringPreferencesKey(INSTALLATION_ID_KEY_NAME)
        }
    }
