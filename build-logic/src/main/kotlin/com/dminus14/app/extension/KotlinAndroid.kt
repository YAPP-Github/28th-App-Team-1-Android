package com.dminus14.app.extension

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

/**
 * Android Application의 공통 SDK와 Java/Kotlin JVM target을 구성한다.
 *
 * [AndroidApplicationConventionPlugin]이 `com.android.application` 적용 후 호출한다.
 * applicationId, 배포 버전과 기능별 의존성은 구성하지 않는다.
 */
internal fun Project.configureKotlinAndroid(applicationExtension: ApplicationExtension) {
    applicationExtension.apply {
        compileSdk {
            version = release(BuildConfig.ANDROID_COMPILE_SDK)
        }
        defaultConfig {
            minSdk = BuildConfig.ANDROID_MIN_SDK
        }
        compileOptions {
            sourceCompatibility = BuildConfig.JAVA_VERSION
            targetCompatibility = BuildConfig.JAVA_VERSION
        }
    }
    configureKotlinJvmTarget()
}

/**
 * Android Library의 공통 SDK와 Java/Kotlin JVM target을 구성한다.
 *
 * [AndroidLibraryConventionPlugin]이 `com.android.library` 적용 후 호출한다.
 * namespace와 기능별 의존성은 각 모듈 또는 capability plugin이 구성한다.
 */
internal fun Project.configureKotlinAndroid(libraryExtension: LibraryExtension) {
    libraryExtension.apply {
        compileSdk {
            version = release(BuildConfig.ANDROID_COMPILE_SDK)
        }
        defaultConfig {
            minSdk = BuildConfig.ANDROID_MIN_SDK
        }
        compileOptions {
            sourceCompatibility = BuildConfig.JAVA_VERSION
            targetCompatibility = BuildConfig.JAVA_VERSION
        }
    }
    configureKotlinJvmTarget()
}

/**
 * 순수 Kotlin/JVM 모듈의 Java/Kotlin JVM target을 구성한다.
 *
 * [JvmLibraryConventionPlugin]이 Kotlin JVM plugin 적용 후 호출하며 Android 설정은 추가하지 않는다.
 */
internal fun Project.configureJvmLibrary() {
    extensions.configure(JavaPluginExtension::class.java) {
        sourceCompatibility = BuildConfig.JAVA_VERSION
        targetCompatibility = BuildConfig.JAVA_VERSION
    }
    configureKotlinJvmTarget()
}

/**
 * 현재 프로젝트에 존재하는 Kotlin Android 또는 Kotlin JVM compiler extension에 공통 JVM target을 설정한다.
 *
 * Android/KMP target은 별도 DSL을 사용하므로 이 함수의 대상이 아니다.
 */
private fun Project.configureKotlinJvmTarget() {
    extensions.findByType(KotlinAndroidProjectExtension::class.java)?.compilerOptions {
        jvmTarget.set(BuildConfig.KOTLIN_JVM_TARGET)
    }
    extensions.findByType(KotlinJvmProjectExtension::class.java)?.compilerOptions {
        jvmTarget.set(BuildConfig.KOTLIN_JVM_TARGET)
    }
}
