plugins {
    alias(libs.plugins.dminus14.android.feature)
}

android {
    namespace = "com.dminus14.app.feature.interviewreport"
}

dependencies {
    api(project(":feature:interviewreport:api"))

    implementation(project(":core:common"))
    implementation(project(":designsystem"))
    implementation(project(":domain"))

    implementation(libs.androidx.media3.exoplayer)

    testImplementation(libs.kotlinx.coroutines.test)
}
