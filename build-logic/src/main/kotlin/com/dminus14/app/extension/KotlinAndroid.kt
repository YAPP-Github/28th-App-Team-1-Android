/**
 * Android/Kotlin/JVM 공통 빌드 설정 헬퍼.
 *
 * compileSdk, minSdk, Java/Kotlin JVM 타겟 등 모듈 타입에 관계없이 공유하는 설정을 정의한다.
 * AGP 9부터 block API(defaultConfig, compileOptions 등)가 [ApplicationExtension]/[LibraryExtension]으로 분리되어 타입별 overload를 사용한다.
 */
package com.dminus14.app.extension

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

/** Application 모듈용 SDK/JVM 공통 설정 */
internal fun Project.configureKotlinAndroid(
    applicationExtension: ApplicationExtension,
) {
    applicationExtension.apply {
        compileSdk {
            version = release(37)
        }
        defaultConfig {
            minSdk = 30
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }
    configureKotlinJvmTarget()
}

/** Library 모듈용 SDK/JVM 공통 설정 */
internal fun Project.configureKotlinAndroid(
    libraryExtension: LibraryExtension,
) {
    libraryExtension.apply {
        compileSdk {
            version = release(37)
        }
        defaultConfig {
            minSdk = 30
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }
    configureKotlinJvmTarget()
}

/** 순수 JVM 모듈(:domain)용 Java/Kotlin JVM 타겟 설정 */
internal fun Project.configureJvmLibrary() {
    extensions.configure(JavaPluginExtension::class.java) {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    configureKotlinJvmTarget()
}

private fun Project.configureKotlinJvmTarget() {
    extensions.findByType(KotlinAndroidProjectExtension::class.java)?.compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
    extensions.findByType(KotlinJvmProjectExtension::class.java)?.compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}
