package com.dminus14.app.feature.interview.media

import android.content.Context
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(UnstableApi::class)
class Media3InterviewMediaTransformer
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : InterviewMediaTransformer {
        override suspend fun export(
            inputFiles: List<File>,
            outputFile: File,
            audioOnly: Boolean,
        ) {
            require(inputFiles.isNotEmpty())
            outputFile.parentFile?.mkdirs()
            val items =
                inputFiles.map { file ->
                    EditedMediaItem
                        .Builder(MediaItem.fromUri(file.toUri()))
                        .setRemoveVideo(audioOnly)
                        .build()
                }
            val sequence = EditedMediaItemSequence.Builder(items).build()
            val composition = Composition.Builder(listOf(sequence)).build()
            suspendCancellableCoroutine { continuation ->
                val transformer =
                    Transformer
                        .Builder(context)
                        .addListener(
                            object : Transformer.Listener {
                                override fun onCompleted(
                                    composition: Composition,
                                    exportResult: ExportResult,
                                ) {
                                    if (continuation.isActive) continuation.resume(Unit)
                                }

                                override fun onError(
                                    composition: Composition,
                                    exportResult: ExportResult,
                                    exportException: ExportException,
                                ) {
                                    if (continuation.isActive) {
                                        continuation.resumeWithException(exportException)
                                    }
                                }
                            },
                        ).build()
                continuation.invokeOnCancellation { transformer.cancel() }
                transformer.start(composition, outputFile.absolutePath)
            }
        }
    }
