plugins {
    alias(libs.plugins.dminus14.android.application)
    alias(libs.plugins.dminus14.android.compose)
    alias(libs.plugins.dminus14.compose.preview)
    alias(libs.plugins.dminus14.compose.resources)
    alias(libs.plugins.dminus14.android.hilt)
    alias(libs.plugins.dminus14.android.navigation3)
    alias(libs.plugins.dminus14.android.test)
    alias(libs.plugins.dminus14.android.compose.test)
    alias(libs.plugins.dminus14.android.quality)
    alias(libs.plugins.dminus14.android.compose.lint)
}

android {
    lint {
        disable.add("MissingClass")
    }
}

dependencies {
    implementation(project(":feature:main:impl"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
}
