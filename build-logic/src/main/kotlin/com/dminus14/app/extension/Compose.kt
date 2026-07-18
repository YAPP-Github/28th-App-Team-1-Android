package com.dminus14.app.extension

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Android Application에서 Compose build feature와 제품 UI 의존성을 구성한다.
 *
 * [AndroidComposeConventionPlugin]이 Android Application plugin 적용을 확인한 뒤 호출한다.
 * Preview, 공용 리소스, 테스트, lint와 Activity Compose는 구성하지 않는다.
 */
internal fun Project.configureAndroidCompose(applicationExtension: ApplicationExtension) {
    applicationExtension.buildFeatures {
        compose = true
    }
    addComposeDependencies()
}

/**
 * Android Library에서 Compose build feature와 제품 UI 의존성을 구성한다.
 *
 * [AndroidComposeConventionPlugin]이 Android Library plugin 적용을 확인한 뒤 호출한다.
 * Preview, 공용 리소스, 테스트와 lint는 구성하지 않는다.
 */
internal fun Project.configureAndroidCompose(libraryExtension: LibraryExtension) {
    libraryExtension.buildFeatures {
        compose = true
    }
    addComposeDependencies()
}

/** Android Compose 제품 코드에 공통으로 필요한 BOM, UI, Graphics와 Material3를 추가한다. */
private fun Project.addComposeDependencies() {
    dependencies {
        val bom = libs.findLibrary("androidx-compose-bom").get()
        add("implementation", platform(bom))
        add("implementation", libs.findLibrary("androidx-compose-ui").get())
        add("implementation", libs.findLibrary("androidx-compose-ui-graphics").get())
        add("implementation", libs.findLibrary("androidx-compose-material3").get())
    }
}
