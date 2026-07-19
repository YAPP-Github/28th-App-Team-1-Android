package com.dminus14.app.data.local.datasource

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences

sealed interface PreferenceEdit {
    fun applyTo(preferences: MutablePreferences)

    data class Set<T : Any>(
        val key: Preferences.Key<T>,
        val value: T,
    ) : PreferenceEdit {
        override fun applyTo(preferences: MutablePreferences) {
            preferences[key] = value
        }
    }

    data class Remove(
        val key: Preferences.Key<*>,
    ) : PreferenceEdit {
        override fun applyTo(preferences: MutablePreferences) {
            preferences.remove(key)
        }
    }
}
