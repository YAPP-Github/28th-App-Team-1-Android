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
include(":feature:main:api")
include(":feature:main:impl")
include(":core:permission")
include(":designsystem")
include(":catalog")
