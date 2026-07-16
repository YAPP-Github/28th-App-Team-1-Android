package com.dminus14.app.convention

import com.dminus14.app.extension.libs
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Android Compose 모듈에 Slack Compose 전용 Android Lint check를 추가한다.
 *
 * Plugin ID: `dminus14.android.compose.lint`
 *
 * Android Compose와 Android Lint Convention Plugin이 먼저 적용되어야 한다. 일반 Android Lint,
 * Spotless와 Detekt 설정은 소유하지 않는다.
 */
class AndroidComposeLintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            dependencies.add(
                "lintChecks",
                libs.findLibrary("slack.compose.lint.checks").get(),
            )

            afterEvaluate {
                check(pluginManager.hasPlugin(pluginId("dminus14-android-compose"))) {
                    "dminus14.android.compose.lint를 사용하려면 dminus14.android.compose가 필요합니다."
                }
                check(pluginManager.hasPlugin(pluginId("dminus14-android-lint"))) {
                    "dminus14.android.compose.lint를 사용하려면 dminus14.android.lint가 필요합니다."
                }
            }
        }
}
