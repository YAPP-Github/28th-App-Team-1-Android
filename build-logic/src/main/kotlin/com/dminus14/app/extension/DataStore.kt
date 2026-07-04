/**
 * DataStore 의존성 헬퍼.
 *
 * Preferences DataStore runtime 의존성을 추가한다.
 */
package com.dminus14.app.extension

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureDataStore() {
    dependencies {
        add("implementation", libs.findLibrary("androidx-datastore-preferences").get())
    }
}
