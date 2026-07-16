package com.dminus14.app.convention

import com.dminus14.app.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Android 앱과 Feature impl에 프로젝트 표준 Navigation 3 의존성을 제공한다.
 *
 * Plugin ID: `dminus14.android.navigation3`
 *
 * Navigation 3 runtime/UI, Navigation entry용 ViewModel과 Hilt Compose bridge만 추가한다.
 * 앱 수준 back stack과 `NavDisplay` 조립은 계속 `:app`이 소유한다.
 */
class AndroidNavigation3ConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            dependencies {
                add("implementation", libs.findLibrary("androidx-hilt-navigation-compose").get())
                add("implementation", libs.findLibrary("androidx-navigation3-runtime").get())
                add("implementation", libs.findLibrary("androidx-navigation3-ui").get())
                add(
                    "implementation",
                    libs.findLibrary("androidx-lifecycle-viewmodel-navigation3").get(),
                )
            }
        }
}
