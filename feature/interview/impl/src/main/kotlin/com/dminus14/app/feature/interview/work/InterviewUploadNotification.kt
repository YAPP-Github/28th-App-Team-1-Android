package com.dminus14.app.feature.interview.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class InterviewUploadNotification
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        /**
         * 이 라이브러리 모듈에는 AndroidManifest가 없어 lint가 dataSync 타입 선언을 확인하지 못한다.
         * `app/src/main/AndroidManifest.xml`의 SystemForegroundService가 이미 dataSync를 선언한다.
         */
        @Suppress("SpecifyForegroundServiceType")
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
            return ForegroundInfo(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
            )
        }

        private companion object {
            const val CHANNEL_ID = "interview_upload"
            const val NOTIFICATION_ID = 136
        }
    }
