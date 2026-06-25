/**
 * Application 모듈 전용 Android 설정 헬퍼.
 *
 * namespace, applicationId, 버전 정보, testInstrumentationRunner, release buildType 등
 * `:app`에만 필요한 설정을 중앙에서 관리한다.
 */
package com.dminus14.app.extension

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project

internal fun Project.configureAndroidApplication(
    extension: ApplicationExtension,
) {
    extension.apply {
        namespace = "com.dminus14.app"
        defaultConfig {
            applicationId = "com.dminus14.app"
            targetSdk = 36
            versionCode = 1
            versionName = "1.0"
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        buildTypes {
            release {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                )
            }
        }
    }
}
