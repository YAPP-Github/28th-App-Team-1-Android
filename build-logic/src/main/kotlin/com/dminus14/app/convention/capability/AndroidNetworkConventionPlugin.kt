package com.dminus14.app.convention.capability

import com.dminus14.app.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * API 통신을 수행하는 모듈에 프로젝트 표준 네트워크 client 의존성을 제공한다.
 *
 * Plugin ID: `dminus14.android.network`
 *
 * Retrofit, Gson converter, logging-interceptor 의존성만 추가하며 Android Library plugin과 API 정의는
 * 소유하지 않는다. 로그 레벨을 언제 어떻게 적용할지(로깅 정책)는 이 Plugin이 결정하지 않으며, 의존 모듈이
 * `HttpLoggingInterceptor`를 직접 구성해 결정한다.
 */
class AndroidNetworkConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            dependencies {
                add("implementation", libs.findLibrary("retrofit2").get())
                add("implementation", libs.findLibrary("retrofit2-converter-gson").get())
                add("implementation", libs.findLibrary("okhttp-logging-interceptor").get())
            }
        }
}
