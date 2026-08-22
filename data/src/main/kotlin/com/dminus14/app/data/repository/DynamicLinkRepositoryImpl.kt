package com.dminus14.app.data.repository

import android.net.Uri
import android.util.Log
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
                    .androidBehavior(DynamicLink.BEHAVIOR_APP)
                    .iosBehavior(DynamicLink.BEHAVIOR_APP)
                    .build()
                    .addOnSuccessListener { dynamicLink ->
                        continuation.resume(dynamicLink.uri.toString())
                    }.addOnFailureListener { error ->
                        // 이 실패는 CreateFeedbackShareDynamicLinkUseCase 에서 원시 딥링크로
                        // 조용히 대체되어 사용자에게 드러나지 않는다. 로그로라도 남기지 않으면
                        // 대시보드 설정이 틀어져도 아무도 모른 채 공유 불가능한 raw 딥링크만
                        // 계속 나가게 된다(#175 원인).
                        Log.w(TAG, "ChottuLink createDynamicLink 실패: $deepLink", error)
                        continuation.resumeWithException(error)
                    }
            }

        private companion object {
            private const val TAG = "DynamicLinkRepository"

            // 대시보드에 등록된 ChottuLink 도메인.
            // selectedPath 는 지정하지 않는다 — 이전에 "report"로 고정 지정했었는데, 이는 이
            // 저장소를 쓰는 지인 피드백 공유 링크와 무관한 값이라 매번 생성이 실패하고
            // (CreateFeedbackShareDynamicLinkUseCase 가) 공유 불가능한 원시 hilit:// 딥링크로
            // 조용히 대체되고 있었다(#175).
            const val CHOTTULINK_DOMAIN = "hilit.chottu.link"
        }
    }
