plugins {
    alias(libs.plugins.dminus14.android.library)
    alias(libs.plugins.dminus14.android.hilt)
    alias(libs.plugins.dminus14.android.lint)
    alias(libs.plugins.dminus14.android.compose)
}

android {
    namespace = "com.dminus14.app.core.permission"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    testImplementation(libs.junit)
}
