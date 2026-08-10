package com.dminus14.app.feature.interview.device

import android.content.Context
import android.os.StatFs
import com.dminus14.app.feature.interview.InterviewConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidInterviewStorageChecker
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : InterviewStorageChecker {
        override fun check(): InterviewStorageStatus {
            val availableBytes = StatFs(context.noBackupFilesDir.path).availableBytes
            return InterviewStorageStatus(
                availableBytes = availableBytes,
                hasEnoughSpace = availableBytes >= InterviewConstants.REQUIRED_STORAGE_BYTES,
            )
        }
    }
