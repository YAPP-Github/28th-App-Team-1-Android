package com.dminus14.app.convention

import com.dminus14.app.extension.addComposeMultiplatformLibraries
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Catalog 모듈 Convention Plugin.
 *
 * Plugin ID: `dminus14.catalog`
 * 적용 대상: `:catalog` 모듈
 *
 * 카탈로그 빌드에 필요한 의존성과 빌드 구성을 적용한다.
 */
@OptIn(ExperimentalWasmDsl::class)
class CatalogConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply(pluginId("dminus14-compose-multiplatform"))

            extensions.configure<KotlinMultiplatformExtension>("kotlin") {
                wasmJs {
                    browser()
                    binaries.executable()
                }

                sourceSets.named("wasmJsMain") {
                    dependencies {
                        implementation(project(":designsystem"))

                        addComposeMultiplatformLibraries(this.project)
                    }
                }
            }

            extensions.configure<ComposeExtension>("compose") {
                (this as ExtensionAware).extensions.configure<ResourcesExtension>("resources") {
                    publicResClass = false
                    packageOfResClass = "com.dminus14.catalog.generated.resources"
                    generateResClass = ResourcesExtension.ResourceClassGeneration.Always
                }
            }
        }
}
