/*
 * build-logic 모듈 빌드 설정.
 *
 * 책임별 Convention Plugin 25종을 등록하고 AGP/Kotlin/KSP Gradle Plugin 의존성을 제공한다.
 * 루트 [settings.gradle.kts]의 includeBuild("build-logic")로 composite build에 포함된다.
 */
plugins {
    `kotlin-dsl`
    alias(libs.plugins.spotless)
}

group = "com.dminus14.app.buildlogic"

// build-logic는 자신이 정의하는 Convention Plugin을 적용할 수 없어 외부 Spotless를 직접 사용한다.
spotless {
    kotlin {
        target("src/**/*.kt")
        targetExclude("**/build/**")
        ktlint()
    }
    kotlinGradle {
        target("build.gradle.kts")
        targetExclude("**/build/**")
        ktlint()
    }
}

// id → implementationClass 매핑으로 서브모듈에서 plugins { id("dminus14.android.*") } 형태로 적용
gradlePlugin {
    plugins {
        register("spotlessConvention") {
            id = "dminus14.spotless"
            implementationClass = "com.dminus14.app.convention.quality.SpotlessConventionPlugin"
        }
        register("detektConvention") {
            id = "dminus14.detekt"
            implementationClass = "com.dminus14.app.convention.quality.DetektConventionPlugin"
        }
        register("kotlinQualityConvention") {
            id = "dminus14.kotlin.quality"
            implementationClass =
                "com.dminus14.app.convention.quality.KotlinQualityConventionPlugin"
        }
        register("androidLintConvention") {
            id = "dminus14.android.lint"
            implementationClass = "com.dminus14.app.convention.quality.AndroidLintConventionPlugin"
        }
        register("androidComposeLintConvention") {
            id = "dminus14.android.compose.lint"
            implementationClass =
                "com.dminus14.app.convention.quality.AndroidComposeLintConventionPlugin"
        }
        register("androidQualityConvention") {
            id = "dminus14.android.quality"
            implementationClass =
                "com.dminus14.app.convention.quality.AndroidQualityConventionPlugin"
        }
        register("androidApplication") {
            id = "dminus14.android.application"
            implementationClass =
                "com.dminus14.app.convention.base.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "dminus14.android.library"
            implementationClass =
                "com.dminus14.app.convention.base.AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "dminus14.android.feature"
            implementationClass =
                "com.dminus14.app.convention.composite.AndroidFeatureConventionPlugin"
        }
        register("androidCompose") {
            id = "dminus14.android.compose"
            implementationClass =
                "com.dminus14.app.convention.capability.AndroidComposeConventionPlugin"
        }
        register("composePreview") {
            id = "dminus14.compose.preview"
            implementationClass =
                "com.dminus14.app.convention.capability.ComposePreviewConventionPlugin"
        }
        register("composeResources") {
            id = "dminus14.compose.resources"
            implementationClass =
                "com.dminus14.app.convention.capability.ComposeResourcesConventionPlugin"
        }
        register("androidHilt") {
            id = "dminus14.android.hilt"
            implementationClass =
                "com.dminus14.app.convention.capability.AndroidHiltConventionPlugin"
        }
        register("androidNavigation3") {
            id = "dminus14.android.navigation3"
            implementationClass =
                "com.dminus14.app.convention.capability.AndroidNavigation3ConventionPlugin"
        }
        register("androidTest") {
            id = "dminus14.android.test"
            implementationClass =
                "com.dminus14.app.convention.capability.AndroidTestConventionPlugin"
        }
        register("androidComposeTest") {
            id = "dminus14.android.compose.test"
            implementationClass =
                "com.dminus14.app.convention.capability.AndroidComposeTestConventionPlugin"
        }
        register("androidRoom") {
            id = "dminus14.android.room"
            implementationClass =
                "com.dminus14.app.convention.capability.AndroidRoomConventionPlugin"
        }
        register("androidNetwork") {
            id = "dminus14.android.network"
            implementationClass =
                "com.dminus14.app.convention.capability.AndroidNetworkConventionPlugin"
        }
        register("androidDataStore") {
            id = "dminus14.android.datastore"
            implementationClass =
                "com.dminus14.app.convention.capability.AndroidDataStoreConventionPlugin"
        }
        register("jvmLibrary") {
            id = "dminus14.jvm.library"
            implementationClass =
                "com.dminus14.app.convention.base.JvmLibraryConventionPlugin"
        }
        register("kotlinMultiplatformLibrary") {
            id = "dminus14.kotlin.multiplatform.library"
            implementationClass =
                "com.dminus14.app.convention.base.KotlinMultiplatformLibraryConventionPlugin"
        }
        register("composeMultiplatformConvention") {
            id = "dminus14.compose.multiplatform"
            implementationClass =
                "com.dminus14.app.convention.base.ComposeMultiplatformConventionPlugin"
        }
        register("composeMultiplatformUiLibraryConvention") {
            id = "dminus14.compose.multiplatform.ui-library"
            implementationClass =
                "com.dminus14.app.convention.base.ComposeMultiplatformUiLibraryConventionPlugin"
        }
        register("composeMultiplatformLibraryConvention") {
            id = "dminus14.compose.multiplatform.library"
            implementationClass =
                "com.dminus14.app.convention.base.ComposeMultiplatformLibraryConventionPlugin"
        }
        register("composeMultiplatformWasmApplicationConvention") {
            id = "dminus14.compose.multiplatform.wasm-application"
            implementationClass =
                "com.dminus14.app.convention.base.ComposeMultiplatformWasmApplicationConventionPlugin"
        }
    }
}

dependencies {
    // Convention Plugin에서 ApplicationExtension 등 AGP DSL을 런타임에 참조하기 위해 implementation 사용
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
    implementation(libs.hilt.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.spotless.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin.api)
    implementation(libs.compose.gradle.plugin)
}
