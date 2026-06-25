/**
 * 네트워크 의존성 헬퍼.
 *
 * Retrofit, Gson converter, OkHttp, logging-interceptor 의존성을 추가한다.
 * `:data` 등 API 통신 모듈에서 [AndroidNetworkConventionPlugin]과 함께 사용한다.
 */
package com.dminus14.app.extension

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureNetwork() {
    dependencies {
        add("implementation", libs.findLibrary("retrofit2").get())
        add("implementation", libs.findLibrary("retrofit2-converter-gson").get())
        add("implementation", libs.findLibrary("squareup-okhttp").get())
        add("implementation", libs.findLibrary("squareup-okhttp-logging-interceptor").get())
    }
}
