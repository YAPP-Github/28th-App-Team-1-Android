package com.dminus14.app.feature.login.di

import javax.inject.Qualifier

/**
 * back stack을 유지한 채 새 destination을 쌓는 navigation 콜백(`Navigator.goTo`)을 가리키는 Qualifier.
 *
 * `feature:login:impl`은 `app`의 `Navigator`에 직접 의존할 수 없으므로, `app`이 이 Qualifier로
 * `(Any) -> Unit` 콜백을 제공하고 여기서는 타입만으로 주입받는다.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GoToNavigation

/**
 * back stack을 비우고 새 destination으로 교체하는 navigation 콜백(`Navigator.replaceAll`)을 가리키는
 * Qualifier.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ReplaceAllNavigation
