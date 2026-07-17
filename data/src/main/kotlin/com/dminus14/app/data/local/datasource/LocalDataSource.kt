package com.dminus14.app.data.local.datasource

import androidx.datastore.preferences.core.Preferences

interface LocalDataSource {
    suspend fun getString(key: Preferences.Key<String>): String?

    suspend fun setString(
        key: Preferences.Key<String>,
        value: String,
    )

    suspend fun remove(key: Preferences.Key<*>)

    suspend fun editAtomically(edits: List<PreferenceEdit>)
}
