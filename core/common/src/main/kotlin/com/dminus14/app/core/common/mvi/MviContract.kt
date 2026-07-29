package com.dminus14.app.core.common.mvi

/**
 * Feature Contract의 Intent marker.
 *
 * Feature별 Intent sealed는 각 Feature에 두고, 이 인터페이스만 구현한다.
 * Intent 타입을 Base에서 공통화하지 않는다.
 */
interface MviIntent

/**
 * Feature Contract의 State marker.
 *
 * Feature별 State data class는 각 Feature에 두고, 이 인터페이스만 구현한다.
 */
interface MviState

/**
 * Feature Contract의 Effect marker.
 *
 * Feature별 Effect sealed는 각 Feature에 두고, 이 인터페이스만 구현한다.
 * Effect가 없는 화면은 빈 sealed interface로 두어도 된다.
 */
interface MviEffect
