/**
 * 순수 Kotlin JVM Library Convention Plugin.
 *
 * Plugin ID: `dminus14.jvm.library`
 * 적용 대상: `:domain` 등 Android 프레임워크에 의존하지 않는 모듈
 *
 * kotlin.jvm plugin을 적용하고 Java/Kotlin JVM 17 타겟을 설정한다.
 */
package com.dminus14.app.convention

import com.dminus14.app.extension.pluginId
import com.dminus14.app.extension.configureJvmLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(pluginId("kotlin-jvm"))
            configureJvmLibrary()
        }
    }
}
