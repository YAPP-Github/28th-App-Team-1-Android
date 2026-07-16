package com.dminus14.app.convention

import com.dminus14.app.extension.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 모든 `:feature:*:impl` 모듈의 표준 Convention Plugin 조합을 제공한다.
 *
 * Plugin ID: `dminus14.android.feature`
 *
 * 이 플러그인은 orchestration만 담당하며 Android DSL이나 dependency를 직접 구성하지 않는다.
 * 각 기능의 실제 설정은 아래에서 적용하는 capability·quality plugin이 소유한다.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(pluginId("dminus14-android-library"))
            pluginManager.apply(pluginId("dminus14-android-compose"))
            pluginManager.apply(pluginId("dminus14-compose-preview"))
            pluginManager.apply(pluginId("dminus14-compose-resources"))
            pluginManager.apply(pluginId("dminus14-android-hilt"))
            pluginManager.apply(pluginId("dminus14-android-navigation3"))
            pluginManager.apply(pluginId("dminus14-android-test"))
            pluginManager.apply(pluginId("dminus14-android-compose-test"))
            pluginManager.apply(pluginId("dminus14-android-quality"))
            pluginManager.apply(pluginId("dminus14-android-compose-lint"))
        }
    }
}
