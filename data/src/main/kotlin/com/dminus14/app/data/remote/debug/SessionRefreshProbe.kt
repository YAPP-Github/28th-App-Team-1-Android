package com.dminus14.app.data.remote.debug

import android.util.Log
import com.dminus14.app.data.remote.api.JobsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TODO(temp): AccessToken 만료 후 TokenAuthenticator 재발급이 동작하는지 확인하기 위한 임시 폴러.
 * Main 진입 시 시작하고, 검증이 끝나면 이 클래스와 JobsApi 관련 코드를 삭제한다.
 */
@Singleton
class SessionRefreshProbe
    @Inject
    constructor(
        private val jobsApi: JobsApi,
    ) {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        private var pollingJob: Job? = null

        fun start() {
            if (pollingJob?.isActive == true) return

            pollingJob =
                scope.launch {
                    Log.d(TAG, "start polling GET /api/v1/jobs every ${INTERVAL_MS}ms")
                    while (isActive) {
                        runCatching { jobsApi.getJobs() }
                            .onSuccess { response ->
                                val count = response.data?.jobs?.size ?: 0
                                Log.d(TAG, "jobs ok count=$count success=${response.success}")
                            }.onFailure { error ->
                                Log.e(TAG, "jobs failed: ${error.message}", error)
                            }
                        delay(INTERVAL_MS)
                    }
                }
        }

        fun stop() {
            pollingJob?.cancel()
            pollingJob = null
            Log.d(TAG, "stopped")
        }

        private companion object {
            const val TAG = "SessionRefreshProbe"
            const val INTERVAL_MS = 10_000L
        }
    }
