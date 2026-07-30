package com.dminus14.app.navigation.di

import com.dminus14.app.feature.login.api.Splash
import com.dminus14.app.feature.login.di.GoToNavigation
import com.dminus14.app.feature.login.di.ReplaceAllNavigation
import com.dminus14.app.navigation.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object NavigatorModule {
    @Provides
    @ActivityRetainedScoped
    fun provideNavigator(): Navigator = Navigator(startDestination = Splash)

    /**
     * `feature:*:impl`은 `app`의 [Navigator]에 직접 의존할 수 없으므로, `(Any) -> Unit` 콜백 형태로
     * 감싸 Qualifier 기반으로 제공한다.
     */
    @Provides
    @ActivityRetainedScoped
    @GoToNavigation
    fun provideGoToNavigation(navigator: Navigator): (Any) -> Unit = navigator::goTo

    @Provides
    @ActivityRetainedScoped
    @ReplaceAllNavigation
    fun provideReplaceAllNavigation(navigator: Navigator): (Any) -> Unit = navigator::replaceAll
}
