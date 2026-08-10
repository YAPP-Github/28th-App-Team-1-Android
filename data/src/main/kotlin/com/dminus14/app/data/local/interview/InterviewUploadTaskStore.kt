package com.dminus14.app.data.local.interview

import com.dminus14.app.domain.model.InterviewUploadTask
import com.google.gson.Gson
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterviewUploadTaskStore
    @Inject
    constructor(
        private val gson: Gson,
        private val fileStore: InterviewFileStore,
    ) {
        fun read(uploadTaskId: String): InterviewUploadTask? {
            val file = taskFile(uploadTaskId)
            if (!file.exists()) return null
            return runCatching {
                gson.fromJson(
                    file.readText(Charsets.UTF_8),
                    InterviewUploadTask::class.java,
                )
            }.getOrNull()
                ?.takeIf { it.schemaVersion == InterviewUploadTask.SCHEMA_VERSION }
                ?: run {
                    fileStore.deleteUpload(uploadTaskId)
                    null
                }
        }

        fun write(task: InterviewUploadTask) {
            val file = taskFile(task.uploadTaskId)
            file.parentFile?.mkdirs()
            val temporary = file.resolveSibling("task.json.tmp")
            temporary.writeText(gson.toJson(task), Charsets.UTF_8)
            if (file.exists()) check(file.delete())
            check(temporary.renameTo(file))
        }

        fun readAll(): List<InterviewUploadTask> =
            fileStore
                .uploadDirectory("")
                .listFiles()
                .orEmpty()
                .filter(File::isDirectory)
                .mapNotNull { read(it.name) }

        private fun taskFile(uploadTaskId: String): File =
            fileStore.uploadDirectory(uploadTaskId).resolve("task.json")
    }
