package com.dminus14.app.convention.capability

import com.dminus14.app.extension.libs
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Android Compose UI 테스트 전용 의존성을 구성한다.
 *
 * Plugin ID: `dminus14.android.compose.test`
 *
 * Android Compose와 Android Test Convention Plugin이 먼저 적용되어야 한다. Compose UI Test
 * JUnit4, Compose BOM과 debug test manifest만 추가하며 실제 테스트 소스는 소유하지 않는다.
 */
class AndroidComposeTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            dependencies {
                val bom = libs.findLibrary("androidx-compose-bom").get()
                add("androidTestImplementation", platform(bom))
                add(
                    "androidTestImplementation",
                    libs.findLibrary("androidx-compose-ui-test-junit4").get(),
                )
                add(
                    "debugImplementation",
                    libs.findLibrary("androidx-compose-ui-test-manifest").get(),
                )
            }

            afterEvaluate {
                check(pluginManager.hasPlugin(pluginId("dminus14-android-compose"))) {
                    "dminus14.android.compose.test를 사용하려면 dminus14.android.compose가 필요합니다."
                }
                check(pluginManager.hasPlugin(pluginId("dminus14-android-test"))) {
                    "dminus14.android.compose.test를 사용하려면 dminus14.android.test가 필요합니다."
                }
            }
        }
}
