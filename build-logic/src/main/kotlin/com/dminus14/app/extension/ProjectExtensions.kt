package com.dminus14.app.extension

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/** 루트 `gradle/libs.versions.toml`을 build-logic에서 조회하기 위한 Version Catalog 접근자다. */
val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * Version Catalog에 정의된 plugin alias를 실제 plugin ID로 변환한다.
 *
 * Convention Plugin이 외부 plugin이나 다른 Convention Plugin을 적용할 때 사용한다.
 *
 * @param alias `libs.versions.toml`의 plugin alias를 Kotlin 접근자 형태로 변환한 이름
 */
internal fun Project.pluginId(alias: String): String =
    libs
        .findPlugin(alias)
        .get()
        .get()
        .pluginId
