package com.dminus14.app.data.local.datasource

import androidx.datastore.preferences.core.Preferences

interface LocalDataSource {
    suspend fun <T> get(key: Preferences.Key<T>): T?

    suspend fun setString(
        key: Preferences.Key<String>,
        value: String,
    )

    suspend fun remove(key: Preferences.Key<*>)

    suspend fun editAtomically(edits: List<PreferenceEdit>)
}
