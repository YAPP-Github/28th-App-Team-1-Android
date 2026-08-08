package com.dminus14.app.convention.composite

import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 모든 `:feature:*:api` 모듈의 표준 Convention Plugin 조합을 제공한다.
 *
 * Plugin ID: `dminus14.jvm.feature-api`
 */
class JvmFeatureApiConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(pluginId("dminus14-jvm-library"))
            pluginManager.apply(pluginId("dminus14-kotlin-navigation-route"))
            pluginManager.apply(pluginId("dminus14-kotlin-quality"))
        }
    }
}
