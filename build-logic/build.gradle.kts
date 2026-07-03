plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("spotlessConvention") {
            id = "dminus14.spotless"
            implementationClass = "com.dminus14.app.convention.SpotlessConventionPlugin"
        }
        register("detektConvention") {
            id = "dminus14.detekt"
            implementationClass = "com.dminus14.app.convention.DetektConventionPlugin"
        }
        register("kotlinLintConvention") {
            id = "dminus14.kotlin.lint"
            implementationClass = "com.dminus14.app.convention.KotlinLintConventionPlugin"
        }
        register("androidLintConvention") {
            id = "dminus14.android.lint"
            implementationClass = "com.dminus14.app.convention.AndroidLintConventionPlugin"
        }
        register("composeMultiplatformConvention") {
            id = "dminus14.compose.multiplatform"
            implementationClass = "com.dminus14.app.convention.ComposeMultiplatformConventionPlugin"
        }
        register("designSystemConvention") {
            id = "dminus14.compose.multiplatform.designsystem"
            implementationClass = "com.dminus14.app.convention.DesignSystemConventionPlugin"
        }
        register("catalogConvention") {
            id = "dminus14.compose.multiplatform.catalog"
            implementationClass = "com.dminus14.app.convention.CatalogConventionPlugin"
        }
    }
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.spotless.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin.api)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.compose.gradle.plugin)
}
