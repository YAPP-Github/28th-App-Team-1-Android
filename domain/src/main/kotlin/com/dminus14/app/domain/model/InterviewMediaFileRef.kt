package com.dminus14.app.domain.model

/** 미디어 파일을 소유한 저장 단위 종류다. */
enum class InterviewMediaOwnerType { SESSION, UPLOAD }

/**
 * 실제 파일 경로를 노출하지 않고 data 저장소에서만 해석하는 불투명 참조다.
 *
 * [ownerType], [ownerId], [segmentType]은 경로가 아니라 위치 힌트이며,
 * data 계층이 전체 저장소를 탐색하지 않고 해당 디렉터리만 조회하도록 사용한다.
 */
data class InterviewMediaFileRef(
    val value: String,
    val ownerType: InterviewMediaOwnerType,
    val ownerId: String,
    val segmentType: InterviewMediaSegmentType? = null,
)
