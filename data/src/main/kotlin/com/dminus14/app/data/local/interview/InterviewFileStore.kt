package com.dminus14.app.data.local.interview

import android.content.Context
import com.dminus14.app.domain.model.InterviewMediaFileRef
import com.dminus14.app.domain.model.InterviewMediaOwnerType
import com.dminus14.app.domain.model.InterviewMediaSegmentType
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("TooManyFunctions")
class InterviewFileStore
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        private val root = context.noBackupFilesDir.resolve("interview")
        private val cacheRoot = context.cacheDir.resolve("interview")

        fun sessionDirectory(sessionId: Long): File = root.resolve(sessionId.toString())

        fun uploadRootDirectory(): File = root.resolve(UPLOAD_DIRECTORY)

        fun uploadDirectory(uploadTaskId: String): File =
            uploadRootDirectory().resolve(requireUploadTaskId(uploadTaskId))

        fun uploadCacheDirectory(uploadTaskId: String): File =
            cacheRoot.resolve(UPLOAD_DIRECTORY).resolve(requireUploadTaskId(uploadTaskId))

        fun create(
            sessionId: Long,
            type: InterviewMediaSegmentType,
            extension: String,
        ): InterviewMediaFileRef {
            val token = UUID.randomUUID().toString()
            createFile(
                sessionDirectory(sessionId).resolve(segmentDirectory(type)),
                token,
                extension,
            )
            return InterviewMediaFileRef(
                value = token,
                ownerType = InterviewMediaOwnerType.SESSION,
                ownerId = sessionId.toString(),
                segmentType = type,
            )
        }

        fun createUploadMediaFile(
            uploadTaskId: String,
            extension: String,
        ): InterviewMediaFileRef {
            val token = UUID.randomUUID().toString()
            createFile(uploadDirectory(uploadTaskId).resolve(MERGED_DIRECTORY), token, extension)
            return InterviewMediaFileRef(
                value = token,
                ownerType = InterviewMediaOwnerType.UPLOAD,
                ownerId = uploadTaskId,
            )
        }

        fun resolve(ref: InterviewMediaFileRef): File {
            require(runCatching { UUID.fromString(ref.value) }.isSuccess) {
                "Invalid media reference"
            }
            return checkNotNull(
                candidateDirectories(ref).firstNotNullOfOrNull { directory ->
                    directory
                        .listFiles()
                        .orEmpty()
                        .firstOrNull { file ->
                            file.isFile && file.nameWithoutExtension == ref.value
                        }
                },
            ) { "Media reference does not exist" }
        }

        fun handoff(
            sessionId: Long,
            uploadTaskId: String,
        ): File {
            val source = sessionDirectory(sessionId)
            val target = uploadDirectory(uploadTaskId)
            check(source.isDirectory) { "Interview session directory does not exist" }
            check(!target.exists()) { "Upload task directory already exists" }
            check(target.parentFile?.let { it.isDirectory || it.mkdirs() } == true) {
                "Failed to create upload directory"
            }
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

        /**
         * 세션 미디어는 업로드 인계 시 세션 디렉터리째 업로드 디렉터리로 이동하므로,
         * 세션 참조는 세션 디렉터리와 업로드 디렉터리의 동일 세그먼트 경로만 순서대로 조회한다.
         */
        private fun candidateDirectories(ref: InterviewMediaFileRef): Sequence<File> =
            when (ref.ownerType) {
                InterviewMediaOwnerType.UPLOAD -> {
                    sequenceOf(uploadDirectory(ref.ownerId).resolve(MERGED_DIRECTORY))
                }

                InterviewMediaOwnerType.SESSION -> {
                    val segment =
                        segmentDirectory(
                            requireNotNull(ref.segmentType) { "Invalid media reference" },
                        )
                    val sessionId =
                        requireNotNull(ref.ownerId.toLongOrNull()) { "Invalid media reference" }
                    sequenceOf(sessionDirectory(sessionId).resolve(segment)) +
                        uploadRootDirectory()
                            .listFiles()
                            .orEmpty()
                            .asSequence()
                            .filter(File::isDirectory)
                            .map { directory -> directory.resolve(segment) }
                }
            }

        private fun createFile(
            directory: File,
            token: String,
            extension: String,
        ) {
            check(directory.isDirectory || directory.mkdirs()) {
                "Failed to create interview media directory"
            }
            val safeExtension =
                extension
                    .trimStart('.')
                    .lowercase()
                    .filter(Char::isLetterOrDigit)
                    .ifEmpty { DEFAULT_EXTENSION }
            check(directory.resolve("$token.$safeExtension").createNewFile()) {
                "Failed to create interview media file"
            }
        }

        private fun segmentDirectory(type: InterviewMediaSegmentType): String =
            type.name.lowercase()

        private fun requireUploadTaskId(uploadTaskId: String): String {
            require(runCatching { UUID.fromString(uploadTaskId) }.isSuccess) {
                "Invalid upload task id"
            }
            return uploadTaskId
        }

        private companion object {
            const val UPLOAD_DIRECTORY = "uploads"
            const val MERGED_DIRECTORY = "merged"
            const val DEFAULT_EXTENSION = "bin"
        }
    }
