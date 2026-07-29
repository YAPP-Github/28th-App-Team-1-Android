package com.dminus14.app.extension

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project
import java.io.File
import java.util.Properties

private const val KEYSTORE_PASSWORD = "KEYSTORE_PASSWORD"
private const val KEYSTORE_DEBUG_FILE = "KEYSTORE_DEBUG_FILE"
private const val KEYSTORE_DEBUG_ALIAS = "KEYSTORE_DEBUG_ALIAS"
private const val KEYSTORE_RELEASE_FILE = "KEYSTORE_RELEASE_FILE"
private const val KEYSTORE_RELEASE_ALIAS = "KEYSTORE_RELEASE_ALIAS"

/**
 * `local.properties`의 KEYSTORE_* 값으로 `:app` signingConfigs를 구성한다.
 *
 * 필요한 속성이나 keystore 파일이 없으면 해당 build type은 기본 서명을 유지한다.
 * 비밀번호·경로·alias는 소스에 하드코딩하지 않고 루트 `local.properties`에서만 읽는다.
 *
 * 기대 키:
 * - `KEYSTORE_PASSWORD`
 * - `KEYSTORE_DEBUG_FILE` / `KEYSTORE_DEBUG_ALIAS`
 * - `KEYSTORE_RELEASE_FILE` / `KEYSTORE_RELEASE_ALIAS`
 */
internal fun Project.configureAndroidSigning(extension: ApplicationExtension) {
    val properties = loadRootLocalProperties()
    val password =
        properties
            .getProperty(KEYSTORE_PASSWORD)
            ?.takeIf { it.isNotBlank() }
            ?: return

    val debugStore = resolveKeystoreFile(properties, KEYSTORE_DEBUG_FILE)
    val debugAlias =
        properties
            .getProperty(KEYSTORE_DEBUG_ALIAS)
            ?.takeIf { it.isNotBlank() }
    val releaseStore = resolveKeystoreFile(properties, KEYSTORE_RELEASE_FILE)
    val releaseAlias =
        properties
            .getProperty(KEYSTORE_RELEASE_ALIAS)
            ?.takeIf { it.isNotBlank() }

    val hasDebugSigning = debugStore != null && debugAlias != null
    val hasReleaseSigning = releaseStore != null && releaseAlias != null
    if (!hasDebugSigning && !hasReleaseSigning) {
        return
    }

    extension.signingConfigs {
        if (hasDebugSigning) {
            getByName("debug") {
                storeFile = debugStore
                storePassword = password
                keyAlias = debugAlias
                keyPassword = password
            }
        }
        if (hasReleaseSigning) {
            maybeCreate("release").apply {
                storeFile = releaseStore
                storePassword = password
                keyAlias = releaseAlias
                keyPassword = password
            }
        }
    }

    if (hasReleaseSigning) {
        extension.buildTypes {
            getByName("release") {
                signingConfig = extension.signingConfigs.getByName("release")
            }
        }
    }
}

private fun Project.loadRootLocalProperties(): Properties =
    Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) {
            file.inputStream().use(::load)
        }
    }

private fun Project.resolveKeystoreFile(
    properties: Properties,
    fileProperty: String,
): File? {
    val relativePath =
        properties
            .getProperty(fileProperty)
            ?.takeIf { it.isNotBlank() }
            ?: return null
    val storeFile = rootProject.file(relativePath)
    return storeFile.takeIf { it.exists() }
}
