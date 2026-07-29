plugins {
    alias(libs.plugins.dminus14.android.feature)
}

android {
    namespace = "com.dminus14.app.feature.home"
}

dependencies {
    api(project(":feature:home:api"))

    implementation(project(":core:common"))
    implementation(project(":designsystem"))
}
