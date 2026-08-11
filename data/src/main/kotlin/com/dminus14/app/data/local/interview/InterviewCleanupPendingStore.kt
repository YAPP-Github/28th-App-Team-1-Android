package com.dminus14.app.data.local.interview

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterviewCleanupPendingStore
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        private val store =
            PreferenceDataStoreFactory.create(
                scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
                produceFile = {
                    context.noBackupFilesDir
                        .resolve("datastore")
                        .apply { mkdirs() }
                        .resolve("interview_cleanup.preferences_pb")
                },
            )

        suspend fun isPending(): Boolean = store.data.first()[IS_PENDING] == true

        suspend fun setPending(isPending: Boolean) {
            store.edit { preferences -> preferences[IS_PENDING] = isPending }
        }

        private companion object {
            val IS_PENDING = booleanPreferencesKey("is_interview_cleanup_pending")
        }
    }
