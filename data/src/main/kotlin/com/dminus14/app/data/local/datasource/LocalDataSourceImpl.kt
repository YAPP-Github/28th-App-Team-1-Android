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
class LocalDataSourceImpl
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) : LocalDataSource {
        override suspend fun <T> get(key: Preferences.Key<T>): T? =
            context.dataStore.data
                .map { preferences -> preferences[key] }
                .first()

        override suspend fun snapshot(): PreferenceSnapshot =
            PreferenceSnapshot(context.dataStore.data.first())

        override suspend fun setString(
            key: Preferences.Key<String>,
            value: String,
        ) {
            context.dataStore.edit { preferences ->
                preferences[key] = value
            }
        }

        override suspend fun remove(key: Preferences.Key<*>) {
            context.dataStore.edit { preferences ->
                preferences.remove(key)
            }
        }

        override suspend fun editAtomically(edits: List<PreferenceEdit>) {
            context.dataStore.edit { preferences ->
                edits.forEach { edit -> edit.applyTo(preferences) }
            }
        }
    }
