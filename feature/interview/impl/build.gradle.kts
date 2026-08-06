plugins {
    alias(libs.plugins.dminus14.android.feature)
}

android {
    namespace = "com.dminus14.app.feature.interview"
}

dependencies {
    api(project(":feature:interview:api"))

    implementation(project(":core:common"))
    implementation(project(":core:permission"))
    implementation(project(":designsystem"))

    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
}
