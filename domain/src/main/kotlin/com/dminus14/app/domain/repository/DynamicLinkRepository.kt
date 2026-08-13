package com.dminus14.app.domain.repository

/**
 * 딥링크를 짧은 공유용 동적 링크로 변환하는 계약이다.
 *
 * 구현은 특정 벤더(예: ChottuLink) SDK에 묶이지만, 이 인터페이스와 상위 usecase는 그 이름을
 * 알지 못한다.
 */
interface DynamicLinkRepository {
    /**
     * [deepLink](예: `hilit://feedback/{token}`)를 감싸는 동적 링크를 생성해 반환한다.
     */
    suspend fun createLink(deepLink: String): String
}
