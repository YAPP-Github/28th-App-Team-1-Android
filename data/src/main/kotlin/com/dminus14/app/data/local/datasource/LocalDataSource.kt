package com.dminus14.app.data.local.datasource

import androidx.datastore.preferences.core.Preferences

interface LocalDataSource {
    suspend fun <T> get(key: Preferences.Key<T>): T?

    /** 여러 키를 같은 Preferences 스냅샷에서 읽기 위한 일관된 조회. */
    suspend fun snapshot(): PreferenceSnapshot

    suspend fun setString(
        key: Preferences.Key<String>,
        value: String,
    )

    suspend fun remove(key: Preferences.Key<*>)

    suspend fun editAtomically(edits: List<PreferenceEdit>)
}
