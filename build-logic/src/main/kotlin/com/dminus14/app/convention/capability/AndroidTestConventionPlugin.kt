package com.dminus14.app.convention.capability

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.dminus14.app.extension.libs
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * 일반 Android 단위 테스트와 계측 테스트의 공통 환경을 구성한다.
 *
 * Plugin ID: `dminus14.android.test`
 *
 * JUnit, AndroidX JUnit, Espresso, AndroidX Test Runner와 instrumentation runner를 제공한다.
 * Compose UI 테스트와 모듈 고유 test fixture는 구성하지 않는다.
 */
class AndroidTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            var configured = false

            pluginManager.withPlugin(pluginId("android-application")) {
                configured = true
                extensions.configure<ApplicationExtension> {
                    defaultConfig.testInstrumentationRunner =
                        "androidx.test.runner.AndroidJUnitRunner"
                }
                addAndroidTestDependencies()
            }
            pluginManager.withPlugin(pluginId("android-library")) {
                configured = true
                extensions.configure<LibraryExtension> {
                    defaultConfig.testInstrumentationRunner =
                        "androidx.test.runner.AndroidJUnitRunner"
                }
                addAndroidTestDependencies()
            }

            afterEvaluate {
                check(configured) {
                    "dminus14.android.test는 Android Application 또는 Library 모듈에만 적용할 수 있습니다."
                }
            }
        }

    /** Android 기본 단위·계측 테스트 의존성을 각 configuration에 추가한다. */
    private fun Project.addAndroidTestDependencies() {
        dependencies {
            add("testImplementation", libs.findLibrary("junit").get())
            add("androidTestImplementation", libs.findLibrary("androidx-junit").get())
            add("androidTestImplementation", libs.findLibrary("androidx-espresso-core").get())
            add("androidTestImplementation", libs.findLibrary("androidx-test-runner").get())
        }
    }
}
