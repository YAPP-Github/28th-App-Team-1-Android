package com.dminus14.app.core.common.event

/** 앱 최상단에서 한 번 처리하는 플랫폼 독립 전역 UI 사건이다. */
sealed interface GlobalAppEvent {
    /** 네트워크 오류 안내 뒤 현재 Activity task를 종료한다. */
    data object ShowNetworkErrorAndExit : GlobalAppEvent

    /** 서버 오류 안내 뒤 현재 Activity task를 종료한다. */
    data object ShowServerErrorAndExit : GlobalAppEvent

    /** 예외 원문을 노출하지 않는 고정 문구를 Toast로 표시한다. */
    data object ShowUnknownError : GlobalAppEvent
}

/** 전역 사건과 영속 작업의 표시 확인 식별자를 함께 전달한다. */
data class GlobalAppEventEnvelope(
    val event: GlobalAppEvent,
    val deliveryId: String? = null,
)
