/**
 * build-logic 모듈 빌드 설정.
 *
 * Convention Plugin 9종을 등록하고, AGP/Kotlin/KSP Gradle Plugin 의존성을 제공한다.
 * 루트 [settings.gradle.kts]의 includeBuild("build-logic")로 composite build에 포함된다.
 */
plugins {
    `kotlin-dsl`
}

group = "com.dminus14.app.buildlogic"

dependencies {
    // Convention Plugin에서 ApplicationExtension 등 AGP DSL을 런타임에 참조하기 위해 implementation 사용
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.ksp.gradlePlugin)
}

// id → implementationClass 매핑으로 서브모듈에서 plugins { id("dminus14.android.*") } 형태로 적용
gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "dminus14.android.application"
            implementationClass = "com.dminus14.app.convention.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "dminus14.android.library"
            implementationClass = "com.dminus14.app.convention.AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "dminus14.android.feature"
            implementationClass = "com.dminus14.app.convention.AndroidFeatureConventionPlugin"
        }
        register("androidCompose") {
            id = "dminus14.android.compose"
            implementationClass = "com.dminus14.app.convention.AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "dminus14.android.hilt"
            implementationClass = "com.dminus14.app.convention.AndroidHiltConventionPlugin"
        }
        register("androidRoom") {
            id = "dminus14.android.room"
            implementationClass = "com.dminus14.app.convention.AndroidRoomConventionPlugin"
        }
        register("androidNetwork") {
            id = "dminus14.android.network"
            implementationClass = "com.dminus14.app.convention.AndroidNetworkConventionPlugin"
        }
        register("androidDataStore") {
            id = "dminus14.android.datastore"
            implementationClass = "com.dminus14.app.convention.AndroidDataStoreConventionPlugin"
        }
        register("jvmLibrary") {
            id = "dminus14.jvm.library"
            implementationClass = "com.dminus14.app.convention.JvmLibraryConventionPlugin"
        }
    }
}
