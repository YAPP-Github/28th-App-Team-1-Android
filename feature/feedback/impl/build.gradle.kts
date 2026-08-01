plugins {
    alias(libs.plugins.dminus14.android.feature)
}

android {
    namespace = "com.dminus14.app.feature.feedback"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":designsystem"))
    implementation(project(":domain"))
    api(project(":feature:feedback:api"))
    implementation(libs.androidx.media3.effect)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui.compose)

    testImplementation(libs.kotlinx.coroutines.test)
}
