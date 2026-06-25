/**
 * build-logic composite build 설정.
 *
 * 루트 프로젝트의 [gradle/libs.versions.toml]을 version catalog로 공유하여
 * Convention Plugin과 동일한 버전/의존성 alias를 사용한다.
 */
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
