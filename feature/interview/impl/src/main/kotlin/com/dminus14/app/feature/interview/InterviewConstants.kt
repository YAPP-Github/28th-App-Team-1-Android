package com.dminus14.app.feature.interview

/**
 * `interview:impl` 내 둘 이상의 Screen이 공유하는 상수.
 * 특정 Screen 하나에만 쓰이는 값은 해당 Contract나 사용처 파일에 둔다.
 */
object InterviewConstants {
    const val MAX_INTERVIEW_SECONDS: Int = 720
    const val CAN_FINISH_INTERVIEW_SECONDS: Int = 480
}
