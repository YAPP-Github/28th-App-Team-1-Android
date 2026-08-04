package com.dminus14.app.domain.model

/**
 * 필수 동의 게이트 상태.
 *
 * [NOT_SUBMITTED]는 최초 동의(신규 온보딩), [STALE]은 재동의, [UP_TO_DATE]는 추가 동의가
 * 필요 없는 최신 상태다.
 */
enum class ConsentPendingStatus {
    NOT_SUBMITTED,
    STALE,
    UP_TO_DATE,
    UNKNOWN,
}

/**
 * 서버가 정의한 동의 항목 코드.
 *
 * 알 수 없는 코드는 [UNKNOWN]으로 흡수하고, 제출·문서 조회에는 [ConsentItem.rawCode]를
 * 그대로 사용한다.
 */
enum class ConsentItemCode {
    AGE_OVER_14,
    TERMS_OF_SERVICE,
    PERSONAL_INFO_COLLECTION,
    INTERVIEW_RECORDING,
    OVERSEAS_TRANSFER,
    UNKNOWN,
    ;

    companion object {
        fun fromRaw(rawCode: String): ConsentItemCode =
            entries.firstOrNull { code -> code.name == rawCode } ?: UNKNOWN
    }
}

/**
 * 지금 동의가 필요한 항목 하나다.
 *
 * Feature UI의 체크 상태는 포함하지 않는다. [hasDocument]가 false면 본문 보기 UI를 숨긴다.
 *
 * @property code 제품에서 인식하는 항목 코드
 * @property rawCode 서버가 내려준 원문 코드. 제출·문서 조회에 사용한다
 * @property label 서버 표시용 제목 (예: `서비스 이용약관`). `(필수)` 접두사는 Feature가 붙인다
 * @property version 지금 동의해야 하는 현행 버전
 * @property isRequired 필수 동의 여부
 * @property hasDocument 해당 버전 본문 존재 여부
 */
data class ConsentItem(
    val code: ConsentItemCode,
    val rawCode: String,
    val label: String,
    val version: Int,
    val isRequired: Boolean,
    val hasDocument: Boolean,
    val isChecked: Boolean = false,
)

/**
 * pending 동의 목록 조회 결과다.
 *
 * [status]가 [ConsentPendingStatus.UP_TO_DATE]이면 [items]는 비어 있을 수 있다.
 */
data class PendingConsentList(
    val status: ConsentPendingStatus,
    val items: List<ConsentItem>,
) {
    /** 필수 항목만 모두 동의하면 제출 가능한지 판정할 때 Feature가 사용한다. */
    val requiredItems: List<ConsentItem>
        get() = items.filter(ConsentItem::isRequired)
}

/**
 * 동의 항목 문서 본문이다.
 *
 * [contentMarkdown]은 마크다운이며, 민감한 실사용자 약관이 아닌 서버 정책 문서다. 로그에
 * 전문을 남기지 않는다.
 */
data class ConsentDocument(
    val code: ConsentItemCode,
    val rawCode: String,
    val title: String,
    val version: Int,
    val contentMarkdown: String,
)

/** 서버로 제출할 동의 항목 하나다. */
data class ConsentSubmissionItem(
    val rawCode: String,
    val version: Int,
    val agreed: Boolean,
)

/**
 * 동의 제출 입력이다.
 *
 * 최초 제출은 필수 항목을 모두 포함하고, 재동의는 pending이 내려준 항목만 보낸다.
 */
data class ConsentSubmission(
    val items: List<ConsentSubmissionItem>,
)
