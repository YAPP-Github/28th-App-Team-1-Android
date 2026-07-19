package com.dminus14.app.convention.capability

import com.dminus14.app.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * 설정 저장이 필요한 모듈에 Preferences DataStore 의존성을 제공한다.
 *
 * Plugin ID: `dminus14.android.datastore`
 *
 * Android Library plugin, serializer와 실제 저장 정책은 구성하지 않는다.
 */
class AndroidDataStoreConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            dependencies {
                add("implementation", libs.findLibrary("androidx-datastore-preferences").get())
            }
        }
}
