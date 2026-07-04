/**
 * Network Convention Plugin.
 *
 * Plugin ID: `dminus14.android.network`
 * 적용 대상: API 통신 모듈 (예: `:data`)
 *
 * Retrofit, OkHttp 의존성을 추가한다. Android Library plugin은 별도로 적용해야 한다.
 */
package com.dminus14.app.convention

import com.dminus14.app.extension.configureNetwork
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidNetworkConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            configureNetwork()
        }
    }
}
