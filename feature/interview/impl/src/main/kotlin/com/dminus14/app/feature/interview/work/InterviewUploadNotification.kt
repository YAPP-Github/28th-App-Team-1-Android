package com.dminus14.app.feature.interview.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class InterviewUploadNotification
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        fun create(): ForegroundInfo {
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "면접 영상 처리",
                    NotificationManager.IMPORTANCE_LOW,
                ),
            )
            val notification =
                NotificationCompat
                    .Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_sys_upload)
                    .setContentTitle("면접 영상을 처리하고 있어요")
                    .setContentText("네트워크 연결 상태에 따라 완료까지 시간이 걸릴 수 있어요")
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)
                    .build()
            return ForegroundInfo(NOTIFICATION_ID, notification)
        }

        private companion object {
            const val CHANNEL_ID = "interview_upload"
            const val NOTIFICATION_ID = 136
        }
    }
