package com.dminus14.app.feature.interview.media

import java.io.File

interface InterviewMediaTransformer {
    suspend fun export(
        inputFiles: List<File>,
        outputFile: File,
        audioOnly: Boolean,
    )
}
