plugins {
    alias(libs.plugins.dminus14.android.feature)
}

android {
    namespace = "com.dminus14.app.feature.home"
}

dependencies {
    api(project(":feature:home:api"))
    api(project(":feature:mypage:api"))
    api(project(":feature:login:api"))
    api(project(":feature:onboarding:api"))
    api(project(":feature:interviewreport:api"))

    implementation(project(":core:common"))
    implementation(project(":designsystem"))
    implementation(project(":domain"))

    testImplementation(libs.kotlinx.coroutines.test)
}
