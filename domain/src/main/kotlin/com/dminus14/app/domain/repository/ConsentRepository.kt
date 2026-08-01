package com.dminus14.app.domain.repository

import com.dminus14.app.domain.model.ConsentDocument
import com.dminus14.app.domain.model.ConsentSubmission
import com.dminus14.app.domain.model.PendingConsentList

/**
 * 약관/동의 pending 목록, 문서 본문, 제출을 담당한다.
 *
 * 동의 문서 본문은 진행 중 메모리에서만 사용하며 이 계약은 로컬 영속 저장을 제공하지 않는다.
 */
interface ConsentRepository {
    /** 지금 동의가 필요한 항목과 게이트 상태를 조회한다. */
    suspend fun getPendingConsentList(): PendingConsentList

    /**
     * [rawCode]와 [version]에 해당하는 동의 문서 본문을 조회한다.
     *
     * pending 목록의 [com.dminus14.app.domain.model.ConsentItem.rawCode]와
     * [com.dminus14.app.domain.model.ConsentItem.version]을 그대로 사용한다.
     */
    suspend fun getConsentDocument(
        rawCode: String,
        version: Int,
    ): ConsentDocument

    /** 검증이 끝난 동의 제출을 서버에 전송한다. */
    suspend fun submitConsent(submission: ConsentSubmission)
}
