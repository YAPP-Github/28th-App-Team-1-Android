/**
 * Gradle Project 확장 유틸리티.
 *
 * Version Catalog(`libs`) 접근 및 catalog 기반 plugin ID 조회 헬퍼를 제공한다.
 */
package com.dminus14.app.extension

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/** 루트 [gradle/libs.versions.toml]에 정의된 Version Catalog */
val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

/** Version Catalog에 정의된 plugin alias로 plugin ID를 조회한다.
 * @param alias catalog에 정의된 plugin name */
internal fun Project.pluginId(alias: String): String =
    libs
        .findPlugin(alias)
        .get()
        .get()
        .pluginId
