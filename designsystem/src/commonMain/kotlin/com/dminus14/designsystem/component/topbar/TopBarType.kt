package com.dminus14.designsystem.component.topbar

/**
 * TopBar 영역 노출 타입.
 *
 * - [Max]: 좌·중앙·우 모두 표시
 * - [HideLeft]: 좌측 숨김
 * - [HideMiddle]: 중앙(타이틀) 숨김
 * - [HideRight]: 우측 숨김
 *
 * 전용 TopBar마다 지원하는 타입이 다를 수 있다. 미지원 조합은 해당 컴포넌트에서 Max와 동일하게 처리한다.
 */
enum class TopBarType {
    Max,
    HideLeft,
    HideMiddle,
    HideRight,
}
