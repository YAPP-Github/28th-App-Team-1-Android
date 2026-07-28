plugins {
    alias(libs.plugins.dminus14.android.feature)
}

android {
    namespace = "com.dminus14.app.feature.feedback"
}

dependencies {
    implementation(project(":designsystem"))
    implementation(project(":domain"))
    implementation(project(":feature:feedback:api"))
}
