package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.exception.FeedbackShareAlreadyExistsException
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.repository.FeedbackShareLocalRepository
import com.dminus14.app.domain.repository.FeedbackShareRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/**
 * 지인 피드백 공유 링크를 생성한다.
 *
 * 성공하면 token 을 [FeedbackShareLocalRepository] 에 저장해, 재진입 시 화면이 "피드백 종료하기"
 * 버튼으로 전환할 수 있게 한다. 딥링크 조립용 token 을 담은 [Result] 를 반환한다.
 */
class CreateFeedbackShareUseCase
    @Inject
    constructor(
        private val feedbackShareRepository: FeedbackShareRepository,
        private val feedbackShareLocalRepository: FeedbackShareLocalRepository,
    ) {
        suspend operator fun invoke(
            sessionId: Long,
            axes: List<GuestFeedbackAxisCode>,
        ): Result<String> =
            runCatchingCancellable {
                try {
                    val token = feedbackShareRepository.createShare(sessionId, axes)
                    feedbackShareLocalRepository.saveToken(sessionId, token)
                    token
                } catch (e: FeedbackShareAlreadyExistsException) {
                    // 서버에는 이미 활성 공유가 있지만, 생성 API 는 충돌 시 기존 token 을 돌려주지
                    // 않는다(create_1 요청/응답 계약에 없음). 실제 token 을 모르는 채로 그냥 실패만
                    // 반환하면, 화면을 나갔다 재진입할 때 로컬에 저장된 token 이 없어 다시 "링크
                    // 생성"을 시도하고 또 같은 충돌을 반복하는 무한 루프가 된다. token 값 자체는
                    // 재진입 시 존재 여부([FeedbackShareLocalRepository.getToken] != null) 판단에만
                    // 쓰이고 딥링크 재구성에는 쓰이지 않으므로, sentinel 을 저장해 서버 상태와 로컬
                    // hasActiveShare 판단을 맞춘다. 종료(closeShare)는 sessionId 만으로 동작해 이
                    // sentinel 로도 정상적으로 공유를 끝낼 수 있다.
                    feedbackShareLocalRepository.saveToken(sessionId, UNKNOWN_ACTIVE_TOKEN)
                    throw e
                }
            }

        private companion object {
            /** 활성 공유는 확인됐지만 실제 token 을 모를 때 저장하는 placeholder. */
            const val UNKNOWN_ACTIVE_TOKEN = "unknown-active-share"
        }
    }
