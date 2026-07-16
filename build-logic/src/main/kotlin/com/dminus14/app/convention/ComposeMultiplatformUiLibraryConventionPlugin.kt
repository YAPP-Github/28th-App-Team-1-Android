package com.dminus14.app.convention

import com.dminus14.app.extension.addComposeMultiplatformUiDependencies
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Android와 Wasm browser에서 공유하는 Compose UI Library 환경을 구성한다.
 *
 * Plugin ID: `dminus14.compose.multiplatform.ui-library`
 *
 * CMP Library target plugin을 조합하고 `commonMain`에 공통 UI 라이브러리만 추가한다.
 * `:designsystem`에서 사용하며 Preview와 `:core:resources`는 별도 capability plugin이 제공한다.
 */
class ComposeMultiplatformUiLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply(pluginId("dminus14-compose-multiplatform-library"))

            pluginManager.withPlugin(pluginId("kotlin-multiplatform")) {
                val kotlinMultiplatformExtension =
                    extensions.getByType(KotlinMultiplatformExtension::class.java)
                kotlinMultiplatformExtension.sourceSets.named("commonMain") {
                    dependencies {
                        addComposeMultiplatformUiDependencies(project)
                    }
                }
            }
        }
}
