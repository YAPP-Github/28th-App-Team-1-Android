plugins {
    alias(libs.plugins.dminus14.android.feature)
}

android {
    namespace = "com.dminus14.app.feature.onboarding"
}

dependencies {
    api(project(":feature:onboarding:api"))

    implementation(project(":core:common"))
    implementation(project(":designsystem"))
    implementation(project(":domain"))
    implementation(project(":feature:interview:api"))

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}
