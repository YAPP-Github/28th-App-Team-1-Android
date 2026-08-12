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
            // TODO(#162): ChottuLink 대시보드에 등록된 실제 도메인 문자열로 교체해야 한다.
            // 이 저장소·문서 어디에도 확정값이 없어 임시값을 둔다. 값이 확정되면 이 상수 한 줄만
            // 바꾸면 된다.
            const val CHOTTULINK_DOMAIN = "REPLACE_WITH_REGISTERED_CHOTTULINK_DOMAIN"
        }
    }
