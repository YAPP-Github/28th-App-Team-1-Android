plugins {
    alias(libs.plugins.dminus14.android.feature)
}

android {
    namespace = "com.dminus14.app.feature.mypage"
}

dependencies {
    api(project(":feature:mypage:api"))

    implementation(project(":core:common"))
    implementation(project(":designsystem"))

    testImplementation(libs.kotlinx.coroutines.test)
}
