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

/**
 * 비회원 참여자 UUID의 기기 내 원본을 앱 전용 Preferences DataStore에 저장한다.
 *
 * 이 DataStore 파일은 앱의 현재 기본 cloud backup 및 device transfer 정책 적용 대상이다.
 */
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
