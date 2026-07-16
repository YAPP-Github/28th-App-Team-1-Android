package com.dminus14.app.convention

import com.dminus14.app.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * API 통신을 수행하는 모듈에 프로젝트 표준 네트워크 client 의존성을 제공한다.
 *
 * Plugin ID: `dminus14.android.network`
 *
 * Retrofit과 Gson converter만 추가하며 Android Library plugin, API 정의와 로깅 정책은 소유하지 않는다.
 */
class AndroidNetworkConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            dependencies {
                add("implementation", libs.findLibrary("retrofit2").get())
                add("implementation", libs.findLibrary("retrofit2-converter-gson").get())
            }
        }
}
