package com.dminus14.app.navigation.di

import androidx.navigation3.runtime.EntryProviderScope
import com.dminus14.app.feature.home.api.Home
import com.dminus14.app.feature.interview.api.InterviewErrorRoute
import com.dminus14.app.feature.interview.interview.InterviewCompletionReason
import com.dminus14.app.feature.interview.navigation.interviewEntryBuilder
import com.dminus14.app.feature.interviewreport.api.InterviewReport
import com.dminus14.app.navigation.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object InterviewNavigationModule {
    @IntoSet
    @Provides
    fun provideInterviewEntryInstaller(navigator: Navigator): EntryProviderScope<Any>.() -> Unit =
        {
            interviewEntryBuilder(
                onNavigateHome = { navigator.replaceAll(Home) },
                onNavigateError = { errorType -> navigator.goTo(InterviewErrorRoute(errorType)) },
                onResumeInterview = navigator::goBack,
                // 정상 종료(COMPLETED)는 홈으로 교체한 뒤 리포트를 push한다. 뒤로가기 시 홈으로
                // 돌아가도록 스택에 홈을 남겨둔다. 중도 이탈(ABANDONED)은 리포트가 없으므로 홈으로만 보낸다.
                onInterviewEnded = { reason, sessionId ->
                    navigator.replaceAll(Home)
                    if (reason == InterviewCompletionReason.COMPLETED) {
                        navigator.goTo(InterviewReport(sessionId = sessionId))
                    }
                },
                onSttAcknowledged = { navigator.replaceAll(Home) },
            )
        }
}
