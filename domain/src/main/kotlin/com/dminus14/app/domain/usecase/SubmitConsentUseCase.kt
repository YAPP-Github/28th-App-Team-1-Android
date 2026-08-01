package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.exception.ConsentValidationException
import com.dminus14.app.domain.model.ConsentSubmission
import com.dminus14.app.domain.repository.ConsentRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/**
 * 동의 제출 입력을 검증하고 서버에 전송한다.
 *
 * 항목이 하나 이상이어야 하며, 코드는 비어 있을 수 없고 버전은 1 이상이어야 한다. 필수 항목
 * 누락 여부는 서버가 pending 목록과 대조해 판정한다.
 */
class SubmitConsentUseCase
    @Inject
    constructor(
        private val consentRepository: ConsentRepository,
    ) {
        suspend operator fun invoke(submission: ConsentSubmission): Result<Unit> =
            runCatchingCancellable {
                validate(submission.items.isNotEmpty(), "동의 항목을 1개 이상 입력해주세요.")
                submission.items.forEach { item ->
                    validate(item.rawCode.trim().isNotEmpty(), "동의 항목 코드가 비어 있습니다.")
                    validate(item.version > 0, "동의 항목 버전이 올바르지 않습니다.")
                }
                val normalized =
                    submission.copy(
                        items =
                            submission.items.map { item ->
                                item.copy(rawCode = item.rawCode.trim())
                            },
                    )
                consentRepository.submitConsent(normalized)
            }

        private fun validate(
            condition: Boolean,
            message: String,
        ) {
            if (!condition) throw ConsentValidationException(message)
        }
    }
