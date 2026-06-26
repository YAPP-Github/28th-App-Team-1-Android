/**
 * DataStore Convention Plugin.
 *
 * Plugin ID: `dminus14.android.datastore`
 * 적용 대상: Preferences DataStore가 필요한 모듈 (예: `:data`)
 *
 * Preferences DataStore 의존성을 추가한다.
 * Android Library plugin은 별도로 적용해야 한다.
 */
package com.dminus14.app.convention

import com.dminus14.app.extension.configureDataStore
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidDataStoreConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            configureDataStore()
        }
    }
}
