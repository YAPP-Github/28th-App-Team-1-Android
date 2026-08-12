package com.dminus14.app.data.repository

import android.net.Uri
import com.chottulink.lib.ChottuLink
import com.chottulink.lib.DynamicLink
import com.dminus14.app.domain.repository.DynamicLinkRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * ChottuLink SDK(`com.chottulink:android-sdk`)로 동적 링크를 생성한다.
 *
 * SDK는 `DMinus14App.onCreate()`에서 [ChottuLink.init]으로 이미 초기화돼 있다고 가정한다
 * (앱 전역 싱글턴이라 이 클래스는 별도 초기화를 하지 않는다).
 */
@Singleton
class DynamicLinkRepositoryImpl
    @Inject
    constructor() : DynamicLinkRepository {
        override suspend fun createLink(deepLink: String): String =
            suspendCancellableCoroutine { continuation ->
                ChottuLink
                    .createDynamicLink()
                    .setLink(Uri.parse(deepLink))
                    .setDomain(CHOTTULINK_DOMAIN)
                    .setSelectedPath(CHOTTULINK_PATH)
                    .androidBehavior(DynamicLink.BEHAVIOR_APP)
                    .iosBehavior(DynamicLink.BEHAVIOR_APP)
                    .build()
                    .addOnSuccessListener { dynamicLink ->
                        continuation.resume(dynamicLink.uri.toString())
                    }.addOnFailureListener { error ->
                        continuation.resumeWithException(error)
                    }
            }

        private companion object {
            // 대시보드에 등록된 ChottuLink 도메인: https://hilit.chottu.link/report
            // setDomain()은 호스트만, 경로(report)는 setSelectedPath()로 분리해서 넘긴다.
            const val CHOTTULINK_DOMAIN = "hilit.chottu.link"
            const val CHOTTULINK_PATH = "report"
        }
    }
