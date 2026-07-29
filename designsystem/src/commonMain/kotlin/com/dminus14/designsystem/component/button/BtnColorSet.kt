package com.dminus14.designsystem.component.button

import androidx.compose.ui.graphics.Color

/**
 * 버튼 활성 상태의 색 세트.
 *
 * disable 색은 포함하지 않는다. disable은 타입별로 별도 토큰을 사용한다.
 *
 * @property contentColor 활성(enable) 텍스트 색.
 * @property backgroundColor 활성(enable) 배경 색.
 * @property pressColor 눌림(press) 배경 색. 텍스트는 [contentColor]를 유지한다.
 */
data class BtnColorSet(
    val contentColor: Color,
    val backgroundColor: Color,
    val pressColor: Color,
)
