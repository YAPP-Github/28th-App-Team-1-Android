package com.dminus14.app.data.local.interview

import com.dminus14.app.domain.model.InterviewMediaManifest
import com.google.gson.Gson
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterviewManifestStore
    @Inject
    constructor(
        private val gson: Gson,
        private val fileStore: InterviewFileStore,
    ) {
        fun read(sessionId: Long): InterviewMediaManifest? =
            read(fileStore.sessionDirectory(sessionId).resolve(FILE_NAME))

        fun readUpload(uploadTaskId: String): InterviewMediaManifest? =
            read(fileStore.uploadDirectory(uploadTaskId).resolve(FILE_NAME))

        fun write(manifest: InterviewMediaManifest) {
            write(fileStore.sessionDirectory(manifest.sessionId).resolve(FILE_NAME), manifest)
        }

        private fun read(file: File): InterviewMediaManifest? {
            if (!file.exists()) return null
            return runCatching {
                gson.fromJson(file.readText(Charsets.UTF_8), InterviewMediaManifest::class.java)
            }.getOrNull()?.takeIf { it.schemaVersion == InterviewMediaManifest.SCHEMA_VERSION }
        }

        private fun write(
            file: File,
            manifest: InterviewMediaManifest,
        ) {
            file.parentFile?.mkdirs()
            val temporary = file.resolveSibling("$FILE_NAME.tmp")
            temporary.writeText(gson.toJson(manifest), Charsets.UTF_8)
            if (file.exists()) check(file.delete())
            check(temporary.renameTo(file))
        }

        private companion object {
            const val FILE_NAME = "manifest.json"
        }
    }
