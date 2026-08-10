plugins {
    alias(libs.plugins.dminus14.android.feature)
}

android {
    namespace = "com.dminus14.app.feature.onboarding"
}

dependencies {
    api(project(":feature:onboarding:api"))
    api(project(":feature:mypage:api"))

    implementation(project(":core:common"))
    implementation(project(":designsystem"))
    implementation(project(":domain"))
}
