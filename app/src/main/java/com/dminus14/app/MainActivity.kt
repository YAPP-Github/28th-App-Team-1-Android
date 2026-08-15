package com.dminus14.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.chottulink.lib.ChottuLink
import com.dminus14.app.error.GlobalErrorHost
import com.dminus14.app.feature.feedback.api.FeedbackOnboarding
import com.dminus14.app.feature.interviewreport.api.InterviewReport
import com.dminus14.app.feature.interviewreport.api.InterviewReportPlayer
import com.dminus14.app.interview.InterviewAppLifecycleCoordinator
import com.dminus14.app.modal.GlobalModalHost
import com.dminus14.app.modal.GlobalModalManager
import com.dminus14.app.navigation.AppNavigationState
import com.dminus14.designsystem.theme.HilitTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigationState: AppNavigationState

    @Inject
    lateinit var globalModalManager: GlobalModalManager

    @Inject
    lateinit var interviewAppLifecycleCoordinator: InterviewAppLifecycleCoordinator

    override fun onStart() {
        super.onStart()
        interviewAppLifecycleCoordinator.onForeground()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleFeedbackShareDeepLink(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navigationState.navigator.onExit = ::finishAffinity
        handleFeedbackShareDeepLink(intent)

        enableEdgeToEdge()
        setContent {
            HilitTheme {
                // interviewReport 관련 화면(리포트·영상 플레이어)은 FullScreen 으로 상태바·
                // 네비게이션바 뒤까지 콘텐츠가 이어져야 해서, 최상위 Scaffold 가 시스템 바 영역을
                // innerPadding 으로 미리 예약하지 않게 한다. 그 화면들은 각자 필요한 요소(닫기
                // 버튼, 하단 컨트롤 등)에만 statusBarsPadding/navigationBarsPadding 을 직접
                // 적용한다. 나머지 화면은 기존과 동일하게 Scaffold 의 기본 inset 을 그대로 받는다.
                val currentRoute = navigationState.navigator.backStack.lastOrNull()
                val isFullScreenRoute =
                    currentRoute is InterviewReport || currentRoute is InterviewReportPlayer
                val contentWindowInsets =
                    if (isFullScreenRoute) WindowInsets(0) else ScaffoldDefaults.contentWindowInsets
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = contentWindowInsets,
                ) { innerPadding ->
                    NavDisplay(
                        backStack = navigationState.navigator.backStack,
                        onBack = navigationState.navigator::goBack,
                        modifier = Modifier.padding(innerPadding),
                        entryDecorators =
                            listOf(
                                rememberSaveableStateHolderNavEntryDecorator(),
                                rememberViewModelStoreNavEntryDecorator(),
                            ),
                        entryProvider =
                            entryProvider {
                                navigationState.entryInstallers.forEach { installer -> installer() }
                            },
                    )
                }

                GlobalModalHost(manager = globalModalManager)
                GlobalErrorHost(
                    onExit = ::finishAffinity,
                    onGlobalEventRendered = interviewAppLifecycleCoordinator::acknowledge,
                )
            }
        }
    }

    /**
     * 지인 피드백 공유 딥링크(`hilit://feedback/{token}`)를 [FeedbackOnboarding] 진입으로 연결한다.
     *
     * ChottuLink SDK가 short link 해석 등 attribution을 함께 처리하도록
     * [ChottuLink.getAppLinkData]로 조회하고, 그 결과가 비어 있으면 커스텀 scheme intent의
     * `data`를 직접 사용한다.
     */
    private fun handleFeedbackShareDeepLink(intent: Intent?) {
        intent ?: return
        ChottuLink
            .getAppLinkData(intent)
            .addOnSuccessListener { result ->
                val link = result?.link ?: intent.data
                navigateToFeedbackOnboarding(link)
            }.addOnFailureListener {
                navigateToFeedbackOnboarding(intent.data)
            }
    }

    private fun navigateToFeedbackOnboarding(link: Uri?) {
        val isFeedbackShareLink =
            link != null &&
                link.scheme == FEEDBACK_SHARE_DEEP_LINK_SCHEME &&
                link.host == FEEDBACK_SHARE_DEEP_LINK_HOST
        val token = link?.lastPathSegment
        if (isFeedbackShareLink && token != null) {
            navigationState.navigator.goTo(FeedbackOnboarding(token))
        }
    }

    private companion object {
        const val FEEDBACK_SHARE_DEEP_LINK_SCHEME = "hilit"
        const val FEEDBACK_SHARE_DEEP_LINK_HOST = "feedback"
    }
}
