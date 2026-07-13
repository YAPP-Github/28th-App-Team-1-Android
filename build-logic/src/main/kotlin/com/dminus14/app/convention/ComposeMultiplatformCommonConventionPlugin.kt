package com.dminus14.app.convention

import com.dminus14.app.extension.addComposeMultiplatformLibraries
import com.dminus14.app.extension.configureKotlinMultiplatformAndroid
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Design System 모듈 Convention Plugin.
 *
 * Plugin ID: `dminus14.designsystem`
 * 적용 대상: `:designsystem` 모듈
 *
 * 디자인 시스템 빌드에 필요한 의존성과 빌드 구성을 적용한다.
 */
class ComposeMultiplatformCommonConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply(pluginId("dminus14-compose-multiplatform"))
            pluginManager.apply(pluginId("android-kmp-library"))

            pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
                extensions.configure<KotlinMultiplatformExtension>("kotlin") {
                    // Android Build Settings
                    configureKotlinMultiplatformAndroid(this)

                    // Library Dependencies
                    sourceSets.named("commonMain") {
                        dependencies {
                            addComposeMultiplatformLibraries(this.project)
                        }
                    }
                }
            }
        }
}
