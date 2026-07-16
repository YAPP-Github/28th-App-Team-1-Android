package com.dminus14.app.convention

import com.dminus14.app.extension.addComposeMultiplatformLibraries
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Android와 Wasm browser를 지원하는 공통 Compose Multiplatform UI Convention Plugin.
 *
 * Compose Multiplatform Library target 구성과 공통 UI 의존성을 적용한다.
 */
class ComposeMultiplatformCommonConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply(pluginId("dminus14-compose-multiplatform-library"))

            pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
                extensions.configure<KotlinMultiplatformExtension>("kotlin") {
                    sourceSets.named("commonMain") {
                        dependencies {
                            addComposeMultiplatformLibraries(this.project)
                        }
                    }
                }
            }
        }
}
