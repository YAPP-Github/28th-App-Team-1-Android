package com.dminus14.app.convention

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.dminus14.app.extension.addComposeMultiplatformLibraries
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Design System 모듈 Convention Plugin.
 *
 * Plugin ID: `dminus14.designsystem`
 * 적용 대상: `:designsystem` 모듈
 *
 * 디자인 시스템 빌드에 필요한 의존성과 빌드 구성을 적용한다.
 */
@OptIn(ExperimentalWasmDsl::class)
class DesignSystemConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply(pluginId("dminus14-compose-multiplatform"))
            pluginManager.apply(pluginId("android-kmp-library"))

            pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
                extensions.configure<KotlinMultiplatformExtension>("kotlin") {
                    // Android Build Settings
                    targets
                        .withType<KotlinMultiplatformAndroidLibraryTarget>()
                        .configureEach {
                            namespace = "com.dminus14.designsystem"

                            compileSdk {
                                version = release(37)
                            }

                            minSdk = 30

                            compilerOptions {
                                jvmTarget.set(JvmTarget.JVM_17)
                            }
                        }

                    // Wasm Build Settings
                    wasmJs {
                        browser()
                    }

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
