package com.dminus14.app.data.local.datasource

import androidx.datastore.preferences.core.Preferences

interface LocalDataSource {
    suspend fun getString(key: Preferences.Key<String>): String?

    suspend fun setString(
        key: Preferences.Key<String>,
        value: String,
    )

    suspend fun getInt(key: Preferences.Key<Int>): Int?

    suspend fun setInt(
        key: Preferences.Key<Int>,
        value: Int,
    )

    suspend fun getBoolean(key: Preferences.Key<Boolean>): Boolean?

    suspend fun setBoolean(
        key: Preferences.Key<Boolean>,
        value: Boolean,
    )

    suspend fun remove(key: Preferences.Key<*>)
}
