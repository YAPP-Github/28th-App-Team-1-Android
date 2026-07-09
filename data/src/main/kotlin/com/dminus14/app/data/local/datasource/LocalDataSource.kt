package com.dminus14.app.data.local.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

@Singleton
class LocalDataSource
@Inject
constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun getString(key: Preferences.Key<String>): String? =
        context.dataStore.data
            .map { preferences -> preferences[key] }
            .first()

    suspend fun setString(
        key: Preferences.Key<String>,
        value: String,
    ) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun getInt(key: Preferences.Key<Int>): Int? =
        context.dataStore.data
            .map { preferences -> preferences[key] }
            .first()

    suspend fun setInt(
        key: Preferences.Key<Int>,
        value: Int,
    ) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun getBoolean(key: Preferences.Key<Boolean>): Boolean? =
        context.dataStore.data
            .map { preferences -> preferences[key] }
            .first()

    suspend fun setBoolean(
        key: Preferences.Key<Boolean>,
        value: Boolean,
    ) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun remove(key: Preferences.Key<*>) {
        context.dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }
}
