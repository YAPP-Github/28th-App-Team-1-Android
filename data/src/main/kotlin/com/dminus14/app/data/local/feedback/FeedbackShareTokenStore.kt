package com.dminus14.app.data.local.feedback

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 지인 피드백 공유 링크 token 을 sessionId 별로 기기에 저장한다.
 *
 * 세션당 활성 링크는 1개뿐이지만(feedback.md), 사용자는 여러 세션(면접)의 리포트를 오갈 수
 * 있어 sessionId 로 동적 키를 만들어 하나의 Preferences 파일에 여러 세션의 token 을 함께
 * 보관한다. [InterviewProgressStore][com.dminus14.app.data.local.interview.InterviewProgressStore]
 * 와 같은 이유로 클라우드 백업 대상에서 제외되는 `noBackupFilesDir` 아래에 둔다.
 */
@Singleton
class FeedbackShareTokenStore
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

        suspend fun getToken(sessionId: Long): String? = store.data.first()[tokenKey(sessionId)]

        suspend fun setToken(
            sessionId: Long,
            token: String,
        ) {
            store.edit { preferences -> preferences[tokenKey(sessionId)] = token }
        }

        suspend fun clearToken(sessionId: Long) {
            store.edit { preferences -> preferences.remove(tokenKey(sessionId)) }
        }

        private fun tokenKey(sessionId: Long) =
            stringPreferencesKey("feedback_share_token_$sessionId")

        private companion object {
            const val FILE_NAME = "feedback_share_token.preferences_pb"
        }
    }
