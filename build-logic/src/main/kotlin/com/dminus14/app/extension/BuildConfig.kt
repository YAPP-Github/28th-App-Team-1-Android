package com.dminus14.app.extension

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/** Repository-wide build values shared by convention plugins. */
internal object BuildConfig {
    const val ANDROID_COMPILE_SDK = 37
    const val ANDROID_MIN_SDK = 30
    const val ANDROID_TARGET_SDK = 36

    const val JVM_VERSION = 17
    val JAVA_VERSION = JavaVersion.toVersion(JVM_VERSION)
    val KOTLIN_JVM_TARGET = JvmTarget.fromTarget(JVM_VERSION.toString())

    const val APPLICATION_VERSION_CODE = 1
    const val APPLICATION_VERSION_NAME = "1.0"
}
