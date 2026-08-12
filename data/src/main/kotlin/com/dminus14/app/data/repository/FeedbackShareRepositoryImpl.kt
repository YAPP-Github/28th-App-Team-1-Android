package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.InterviewRemoteDataSource
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.data.remote.mapper.CommonApiErrorMapper
import com.dminus14.app.domain.exception.UnknownException
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.domain.repository.FeedbackShareRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 호스트 공유 링크 생성 Repository 구현. 인증 Interview 원격 데이터 소스를 재사용한다.
 *
 * 공유 token 은 저장하거나 로그로 남기지 않고 그대로 상위 계층에 반환한다.
 */
@Singleton
class FeedbackShareRepositoryImpl
    @Inject
    constructor(
        private val interviewRemoteDataSource: InterviewRemoteDataSource,
    ) : FeedbackShareRepository {
        override suspend fun createShare(
            sessionId: Long,
            axes: List<GuestFeedbackAxisCode>,
        ): String {
            val response =
                runCatching {
                    interviewRemoteDataSource.createFeedbackShare(
                        sessionId = sessionId,
                        axes = axes.map { it.name },
                    )
                }.getOrElse { error -> throw CommonApiErrorMapper.map(error) }
            return response.token ?: throw UnknownException(errCode = ApiErrorCode.UNKNOWN)
        }
    }
