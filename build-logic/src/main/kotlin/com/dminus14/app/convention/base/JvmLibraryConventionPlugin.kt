package com.dminus14.app.convention.base

import com.dminus14.app.extension.configureJvmLibrary
import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Android Framework에 의존하지 않는 Kotlin/JVM Library 기반을 구성한다.
 *
 * Plugin ID: `dminus14.jvm.library`
 *
 * `:feature:*:api`, 향후 `:domain`처럼 순수 JVM 산출물을 만드는 모듈에 사용한다. Kotlin JVM
 * plugin과 [com.dminus14.app.extension.BuildConfig] 기반 Java/Kotlin JVM target만 구성한다.
 */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(pluginId("kotlin-jvm"))
            configureJvmLibrary()
        }
    }
}
