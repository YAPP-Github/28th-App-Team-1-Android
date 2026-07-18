plugins {
    alias(libs.plugins.dminus14.android.library)
    alias(libs.plugins.dminus14.android.hilt)
    alias(libs.plugins.dminus14.android.compose)
    alias(libs.plugins.dminus14.android.test)
    alias(libs.plugins.dminus14.android.quality)
    alias(libs.plugins.dminus14.android.compose.lint)
}

android {
    namespace = "com.dminus14.app.core.permission"
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
}
