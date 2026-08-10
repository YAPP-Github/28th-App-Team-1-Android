package com.dminus14.app.domain.model

/** 실제 파일 경로를 노출하지 않고 data 저장소에서만 해석하는 불투명 참조다. */
@JvmInline
value class InterviewMediaFileRef(
    val value: String,
)
