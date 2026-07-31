package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.GuestFeedbackApi
import com.dminus14.app.data.remote.dto.GuestFeedbackEntryResponseDto
import com.dminus14.app.data.remote.dto.GuestFeedbackRatingDto
import com.dminus14.app.data.remote.dto.GuestFeedbackSubmitRequestDto
import com.dminus14.app.data.remote.dto.GuestFeedbackSubmitResponseDto
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Guest Feedback Retrofit 호출과 제출 요청 조립만 담당하는 원격 데이터 소스다.
 *
 * 응답 계약 검증은 Gson 응답 adapter가 DTO 생성 전에 완료하므로 여기서는 응답을 저장하거나
 * 다시 검증하지 않고 외부 예외와 함께 그대로 상위 계층에 전달한다.
 */
@Singleton
class GuestFeedbackRemoteDataSourceImpl
    @Inject
    constructor(
        private val guestFeedbackApi: GuestFeedbackApi,
    ) : GuestFeedbackRemoteDataSource {
        /** 공유 token의 진입 정보를 요청하고 adapter가 검증한 DTO를 반환한다. */
        override suspend fun enter(token: String): GuestFeedbackEntryResponseDto =
            guestFeedbackApi.enter(token)

        /** nullable nickname과 comment 키를 보존하는 제출 요청을 조립해 전송한다. */
        override suspend fun submit(
            token: String,
            nickname: String?,
            ratings: List<GuestFeedbackRatingDto>,
        ): GuestFeedbackSubmitResponseDto =
            guestFeedbackApi.submit(
                token = token,
                request =
                    GuestFeedbackSubmitRequestDto(
                        nickname = nickname,
                        ratings = ratings,
                    ),
            )
    }
