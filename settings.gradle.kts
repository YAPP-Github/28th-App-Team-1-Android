pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://devrepo.kakao.com/nexus/content/groups/public/") }

        // Node.js distribution repository
        exclusiveContent {
            forRepository {
                ivy("https://nodejs.org/dist/") {
                    name = "Node.js Distributions"
                    patternLayout {
                        artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]")
                    }
                    metadataSources {
                        artifact()
                    }
                    content {
                        includeModule("org.nodejs", "node")
                    }
                }
            }
            filter {
                includeModule("org.nodejs", "node")
            }
        }

        // Yarn distribution repository
        exclusiveContent {
            forRepository {
                ivy("https://github.com/yarnpkg/yarn/releases/download") {
                    name = "Yarn Distributions"
                    patternLayout {
                        artifact("v[revision]/[artifact]-v[revision].[ext]")
                    }
                    metadataSources {
                        artifact()
                    }
                    content {
                        includeModule("com.yarnpkg", "yarn")
                    }
                }
            }
            filter {
                includeModule("com.yarnpkg", "yarn")
            }
        }

        // Binaryen distribution repository for Kotlin/Wasm
        exclusiveContent {
            forRepository {
                ivy("https://github.com/WebAssembly/binaryen/releases/download") {
                    name = "Binaryen Distributions"
                    patternLayout {
                        artifact(
                            "version_[revision]/[module]-version_[revision]-[classifier].[ext]",
                        )
                    }
                    metadataSources {
                        artifact()
                    }
                }
            }
            filter {
                includeModule("com.github.webassembly", "binaryen")
            }
        }
    }
}

rootProject.name = "DMinus14"

include(":app")
include(":domain")
include(":data")
include(":feature:home:api")
include(":feature:home:impl")
include(":feature:login:api")
include(":feature:login:impl")
include(":feature:feedback:api")
include(":feature:feedback:impl")
include(":feature:mypage:api")
include(":feature:mypage:impl")
include(":feature:onboarding:api")
include(":feature:onboarding:impl")
include(":feature:interview:api")
include(":feature:interview:impl")
include(":feature:interviewreport:api")
include(":feature:interviewreport:impl")
include(":core:permission")
include(":core:common")
include(":designsystem")
include(":catalog")
include(":catalog:annotations")
include(":catalog:processor")
include(":core:resources")
include(":core:crypto")
