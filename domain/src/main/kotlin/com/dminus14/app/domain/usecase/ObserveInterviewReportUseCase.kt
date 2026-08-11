package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.InterviewReport
import com.dminus14.app.domain.model.InterviewReportStatus
import com.dminus14.app.domain.repository.InterviewRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 리포트를 조회하고 [InterviewReportStatus.GENERATING] 이면 폴링해서 완료 상태를 방출한다.
 *
 * - 진입 시 1회 즉시 조회.
 * - `GENERATING` 응답이면 [POLL_INTERVAL_MS] 간격으로 [MAX_ATTEMPTS] 회까지 재조회.
 * - 상한을 넘겨도 여전히 `GENERATING` 이면 [InterviewReportStatus.FAILED] 로 강제 방출해
 *   UI 가 재시도 안내를 노출할 수 있게 한다.
 * - 네트워크/서버 예외는 그대로 전파. 재시도 정책은 상위 ViewModel 이 담당.
 */
class ObserveInterviewReportUseCase
    @Inject
    constructor(
        private val interviewRepository: InterviewRepository,
    ) {
        operator fun invoke(sessionId: Long): Flow<InterviewReport> =
            flow {
                var attempts = 0
                while (true) {
                    val report = interviewRepository.getReport(sessionId)
                    emit(report)
                    if (report.status != InterviewReportStatus.GENERATING) return@flow
                    attempts += 1
                    if (attempts >= MAX_ATTEMPTS) {
                        emit(report.copy(status = InterviewReportStatus.FAILED))
                        return@flow
                    }
                    delay(POLL_INTERVAL_MS)
                }
            }

        internal companion object {
            const val POLL_INTERVAL_MS: Long = 3_000L
            const val MAX_ATTEMPTS: Int = 30
        }
    }
