package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.exception.GuestFeedbackValidationException
import com.dminus14.app.domain.model.GuestFeedbackAxis
import com.dminus14.app.domain.model.GuestFeedbackSubmission
import com.dminus14.app.domain.repository.GuestFeedbackRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/**
 * 비회원 피드백 입력을 검증·정규화하고 한 번 확정 제출한다.
 *
 * 지정된 `1..5`개 항목을 중복이나 누락 없이 모두 평가해야 하며 단계는 `1..4`여야 한다. 별칭과
 * 코멘트는 양끝 공백을 제거하고 빈 별칭은 `익명의 지인`, 빈 코멘트는 빈 문자열로 바꾼다.
 * 코멘트는 문자 종류를 제한하지 않으며 트리밍 후 Kotlin [String.length] 기준 최대 100이다.
 * 검증 실패 시 Repository를 호출하지 않고 coroutine 취소는 [Result] 실패로 감싸지 않는다.
 */
class SubmitGuestFeedbackUseCase
    @Inject
    constructor(
        private val guestFeedbackRepository: GuestFeedbackRepository,
    ) {
        /**
         * 진입 결과의 [axes]와 작성한 [submission]을 검증·정규화해 확정 제출한다.
         *
         * [token]은 트리밍 후 비어 있으면 거부한다. 지정 항목은 중복 없이 모두 평가해야 하고
         * 단계는 `1..4`여야 한다. 빈 별칭은 `익명의 지인`, 빈 코멘트는 빈 문자열로 바꾸며
         * 코멘트는 문자 종류 제한 없이 트리밍 후 [String.length] 100까지 허용한다.
         */
        suspend operator fun invoke(
            token: String,
            axes: List<GuestFeedbackAxis>,
            submission: GuestFeedbackSubmission,
        ): Result<Unit> =
            runCatchingCancellable {
                val normalizedToken = token.trim()
                validate(normalizedToken.isNotEmpty(), "공유 token이 비어 있습니다.")
                validate(axes.size in MIN_AXIS_COUNT..MAX_AXIS_COUNT, "지정 평가 항목 수가 올바르지 않습니다.")

                val expectedAxes = axes.map { axis -> axis.code }
                val submittedAxes = submission.ratings.map { rating -> rating.axis }
                validate(expectedAxes.distinct().size == expectedAxes.size, "지정 평가 항목이 중복되었습니다.")
                validate(submittedAxes.distinct().size == submittedAxes.size, "제출 평가 항목이 중복되었습니다.")
                validate(expectedAxes.toSet() == submittedAxes.toSet(), "지정된 모든 항목을 평가해야 합니다.")
                validate(
                    submission.ratings.all { rating ->
                        rating.level in MIN_LEVEL..MAX_LEVEL
                    },
                    "평가 단계가 올바르지 않습니다.",
                )

                val normalizedSubmission =
                    submission.copy(
                        nickname =
                            submission.nickname
                                ?.trim()
                                .orEmpty()
                                .ifEmpty { ANONYMOUS_NICKNAME },
                        ratings =
                            submission.ratings.map { rating ->
                                val comment = rating.comment?.trim().orEmpty()
                                validate(
                                    comment.length <= MAX_COMMENT_LENGTH,
                                    "항목별 코멘트는 100자 이하여야 합니다.",
                                )
                                rating.copy(comment = comment)
                            },
                    )
                guestFeedbackRepository.submit(
                    token = normalizedToken,
                    submission = normalizedSubmission,
                )
            }

        private fun validate(
            condition: Boolean,
            message: String,
        ) {
            if (!condition) throw GuestFeedbackValidationException(message)
        }

        private companion object {
            const val MIN_AXIS_COUNT = 1
            const val MAX_AXIS_COUNT = 5
            const val MIN_LEVEL = 1
            const val MAX_LEVEL = 4
            const val MAX_COMMENT_LENGTH = 100
            const val ANONYMOUS_NICKNAME = "익명의 지인"
        }
    }
