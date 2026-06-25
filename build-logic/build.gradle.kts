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
    }
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.spotless.gradle.plugin)
}
