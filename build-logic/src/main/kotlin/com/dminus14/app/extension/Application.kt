package com.dminus14.app.extension

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project

/**
 * Android Application 전용 식별자와 배포 설정을 구성한다.
 *
 * [AndroidApplicationConventionPlugin]이 `com.android.application` 적용 후 호출한다.
 * SDK/JVM target, Compose, 테스트와 Android Lint 정책은 구성하지 않는다.
 */
internal fun Project.configureAndroidApplication(extension: ApplicationExtension) {
    extension.apply {
        namespace = "com.dminus14.app"
        defaultConfig {
            applicationId = "com.dminus14.app"
            targetSdk = BuildConfig.ANDROID_TARGET_SDK
            versionCode = BuildConfig.APPLICATION_VERSION_CODE
            versionName = BuildConfig.APPLICATION_VERSION_NAME
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
