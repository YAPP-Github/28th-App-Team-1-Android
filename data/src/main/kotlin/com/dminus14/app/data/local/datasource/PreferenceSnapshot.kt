package com.dminus14.app.data.local.datasource

import androidx.datastore.preferences.core.Preferences

/**
 * [LocalDataSource.snapshot]이 반환하는 단일 Preferences 스냅샷.
 * 여러 키를 같은 시점의 값으로 읽을 때 사용한다.
 */
class PreferenceSnapshot internal constructor(
    private val preferences: Preferences,
) {
    operator fun <T> get(key: Preferences.Key<T>): T? = preferences[key]
}
