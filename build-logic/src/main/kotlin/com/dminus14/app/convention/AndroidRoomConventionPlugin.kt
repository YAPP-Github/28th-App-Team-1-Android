/**
 * Room Convention Plugin.
 *
 * Plugin ID: `dminus14.android.room`
 * 적용 대상: 로컬 DB가 필요한 모듈 (예: `:data`)
 *
 * Room runtime/ktx/compiler 의존성을 추가한다.
 * KSP/Hilt는 [AndroidHiltConventionPlugin]을 별도로 적용해야 한다.
 */
package com.dminus14.app.convention

import com.dminus14.app.extension.configureRoom
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            configureRoom()
        }
    }
}
