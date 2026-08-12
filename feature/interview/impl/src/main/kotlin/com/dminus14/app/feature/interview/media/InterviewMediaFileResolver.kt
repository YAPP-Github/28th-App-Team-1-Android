package com.dminus14.app.feature.interview.media

import com.dminus14.app.domain.model.InterviewMediaFileRef
import java.io.File

/** 불투명 미디어 참조를 Android 미디어 실행기가 사용할 앱 전용 파일로 해석한다. */
fun interface InterviewMediaFileResolver {
    suspend fun resolve(mediaRef: InterviewMediaFileRef): File
}
