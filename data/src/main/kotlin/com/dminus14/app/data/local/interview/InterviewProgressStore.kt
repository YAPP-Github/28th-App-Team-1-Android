package com.dminus14.app.data.local.interview

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.dminus14.app.domain.model.InterviewProgress
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterviewProgressStore
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
                        .resolve(FILE_NAME)
                },
            )

        suspend fun read(): InterviewProgress? {
            val preferences = store.data.first()
            val sessionId = preferences[SESSION_ID] ?: return null
            return InterviewProgress(
                sessionId = sessionId,
                retentionDeadlineEpochMillis = preferences[RETENTION_DEADLINE] ?: 0L,
                retentionRemainingAtCheckpointMillis = preferences[RETENTION_REMAINING] ?: 0L,
                retentionCheckpointElapsedRealtimeMillis = preferences[RETENTION_REALTIME],
                timerStartedAtEpochMillis = preferences[TIMER_STARTED_AT],
                elapsedAtCheckpointMillis = preferences[ELAPSED_AT_CHECKPOINT],
                checkpointedAtEpochMillis = preferences[CHECKPOINTED_AT],
            )
        }

        suspend fun write(progress: InterviewProgress) {
            store.edit { preferences ->
                preferences[SESSION_ID] = progress.sessionId
                preferences[RETENTION_DEADLINE] = progress.retentionDeadlineEpochMillis
                preferences[RETENTION_REMAINING] = progress.retentionRemainingAtCheckpointMillis
                progress.retentionCheckpointElapsedRealtimeMillis.setOrRemove(
                    preferences,
                    RETENTION_REALTIME,
                )
                progress.timerStartedAtEpochMillis.setOrRemove(preferences, TIMER_STARTED_AT)
                progress.elapsedAtCheckpointMillis.setOrRemove(preferences, ELAPSED_AT_CHECKPOINT)
                progress.checkpointedAtEpochMillis.setOrRemove(preferences, CHECKPOINTED_AT)
            }
        }

        suspend fun clear() {
            store.edit { it.clear() }
        }

        private fun Long?.setOrRemove(
            preferences: androidx.datastore.preferences.core.MutablePreferences,
            key: androidx.datastore.preferences.core.Preferences.Key<Long>,
        ) {
            if (this == null) preferences.remove(key) else preferences[key] = this
        }

        private companion object {
            const val FILE_NAME = "interview_progress.preferences_pb"
            val SESSION_ID = longPreferencesKey("session_id")
            val RETENTION_DEADLINE = longPreferencesKey("retention_deadline_epoch_millis")
            val RETENTION_REMAINING = longPreferencesKey("retention_remaining_millis")
            val RETENTION_REALTIME = longPreferencesKey("retention_checkpoint_realtime_millis")
            val TIMER_STARTED_AT = longPreferencesKey("timer_started_at_epoch_millis")
            val ELAPSED_AT_CHECKPOINT = longPreferencesKey("elapsed_at_checkpoint_millis")
            val CHECKPOINTED_AT = longPreferencesKey("checkpointed_at_epoch_millis")
        }
    }
