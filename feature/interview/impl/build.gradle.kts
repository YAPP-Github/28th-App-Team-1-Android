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
    implementation(project(":domain"))
    implementation(project(":designsystem"))

    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.video)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.transformer)
    implementation(libs.androidx.media3.datasource.okhttp)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    testImplementation(libs.androidx.work.testing)
    testImplementation(libs.kotlinx.coroutines.test)
}
