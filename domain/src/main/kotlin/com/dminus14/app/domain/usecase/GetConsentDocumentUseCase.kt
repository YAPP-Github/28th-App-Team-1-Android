package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.exception.ConsentValidationException
import com.dminus14.app.domain.model.ConsentDocument
import com.dminus14.app.domain.repository.ConsentRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/**
 * 동의 항목 문서 본문을 조회한다.
 *
 * pending 목록이 내려준 rawCode와 version을 그대로 사용한다. 빈 코드나 0 이하 버전은
 * Repository를 호출하지 않는다.
 */
class GetConsentDocumentUseCase
    @Inject
    constructor(
        private val consentRepository: ConsentRepository,
    ) {
        suspend operator fun invoke(
            rawCode: String,
            version: Int,
        ): Result<ConsentDocument> =
            runCatchingCancellable {
                val normalizedCode = rawCode.trim()
                if (normalizedCode.isEmpty()) {
                    throw ConsentValidationException("동의 항목 코드가 비어 있습니다.")
                }
                if (version <= 0) {
                    throw ConsentValidationException("동의 문서 버전이 올바르지 않습니다.")
                }
                consentRepository.getConsentDocument(
                    rawCode = normalizedCode,
                    version = version,
                )
            }
    }
