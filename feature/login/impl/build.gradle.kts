plugins {
    alias(libs.plugins.dminus14.android.feature)
}

android {
    namespace = "com.dminus14.app.feature.login"
}

dependencies {
    api(project(":feature:login:api"))
    implementation(project(":core:common"))
    implementation(project(":core:permission"))
    implementation(project(":designsystem"))
    implementation(project(":domain"))
    implementation(project(":feature:home:api"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.kakao.v2.user)

    testImplementation(libs.kotlinx.coroutines.test)
}
