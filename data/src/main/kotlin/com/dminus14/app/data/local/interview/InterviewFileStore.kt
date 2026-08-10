package com.dminus14.app.data.local.interview

import android.content.Context
import com.dminus14.app.domain.model.InterviewMediaFileRef
import com.dminus14.app.domain.model.InterviewMediaSegmentType
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterviewFileStore
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        private val root = context.noBackupFilesDir.resolve("interview")
        private val cacheRoot = context.cacheDir.resolve("interview")

        fun sessionDirectory(sessionId: Long): File = root.resolve(sessionId.toString())

        fun uploadDirectory(uploadTaskId: String): File =
            root.resolve("uploads").resolve(uploadTaskId)

        fun uploadCacheDirectory(uploadTaskId: String): File =
            cacheRoot.resolve("uploads").resolve(uploadTaskId)

        fun create(
            sessionId: Long,
            type: InterviewMediaSegmentType,
            extension: String,
        ): InterviewMediaFileRef {
            val token = UUID.randomUUID().toString()
            val safeExtension =
                extension.trimStart('.').lowercase().filter(Char::isLetterOrDigit)
            val directory =
                sessionDirectory(sessionId)
                    .resolve(type.name.lowercase())
                    .apply { mkdirs() }
            directory.resolve("$token.$safeExtension").createNewFile()
            return InterviewMediaFileRef(token)
        }

        fun createUploadMediaFile(
            uploadTaskId: String,
            extension: String,
        ): InterviewMediaFileRef {
            val token = UUID.randomUUID().toString()
            val safeExtension =
                extension.trimStart('.').lowercase().filter(Char::isLetterOrDigit)
            val directory = uploadDirectory(uploadTaskId).resolve("merged").apply { mkdirs() }
            directory.resolve("$token.$safeExtension").createNewFile()
            return InterviewMediaFileRef(token)
        }

        fun resolve(ref: InterviewMediaFileRef): File {
            require(runCatching { UUID.fromString(ref.value) }.isSuccess) {
                "Invalid media reference"
            }
            return checkNotNull(
                sequenceOf(root, cacheRoot)
                    .flatMap { directory ->
                        if (directory.exists()) {
                            directory.walkTopDown().asSequence()
                        } else {
                            emptySequence()
                        }
                    }.firstOrNull { file -> file.isFile && file.nameWithoutExtension == ref.value },
            ) { "Media reference does not exist" }
        }

        fun handoff(
            sessionId: Long,
            uploadTaskId: String,
        ): File {
            val source = sessionDirectory(sessionId)
            val target = uploadDirectory(uploadTaskId)
            target.parentFile?.mkdirs()
            check(!target.exists()) { "Upload task directory already exists" }
            check(source.renameTo(target)) { "Failed to hand off interview media" }
            return target
        }

        fun deleteSession(sessionId: Long) {
            sessionDirectory(sessionId).deleteRecursively()
        }

        fun deleteUpload(uploadTaskId: String) {
            uploadDirectory(uploadTaskId).deleteRecursively()
            uploadCacheDirectory(uploadTaskId).deleteRecursively()
        }

        fun clearAll() {
            root.deleteRecursively()
            cacheRoot.deleteRecursively()
        }
    }
